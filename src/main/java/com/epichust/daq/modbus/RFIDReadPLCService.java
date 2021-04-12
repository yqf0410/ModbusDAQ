package com.epichust.daq.modbus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.project.rfidCheck.entity.ProduBind;
import com.project.rfidCheck.entity.ProduCheck;
import com.project.rfidCheck.service.ProduBindService;
import com.project.rfidCheck.service.ProduCheckService;
import com.project.rfidCheck.service.TaskLogService;
import com.serotonin.util.queue.ByteQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Date;

@Configuration
@EnableScheduling
public class RFIDReadPLCService {

    @Autowired
    private ProduBindService produBindService;

    @Autowired
    private ProduCheckService produCheckService;

    @Autowired
    private TaskLogService taskLogService;

    @Autowired
    private TCPReader tcpReader;

    @Autowired
    private TCPWriter tcpWriter;

    //3.添加定时任务
    @Scheduled(fixedRate = 1000)
    private void configureTasks() {
        Integer flag = 2;
        String message = "";
        String data = "";
        try {
            System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
            //读取高电平
            //[2,3,2,0,0]
            ByteQueue flagByte = tcpReader.modbusReadWithTCP(com.project.rfidCheck.plc.modbus.CommandConstant.CHECK_FLAG, 1);
            if (flagByte.peek(4) == 1) {
                //读取RFID信息校验
                //[2,3,2,0,11]
                ByteQueue cellByte = tcpReader.modbusReadWithTCP(com.project.rfidCheck.plc.modbus.CommandConstant.CHECK_WORK_CELL, 1);
                //[2,3,28,0,1,0,2,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
                ByteQueue uidByte = tcpReader.modbusReadWithTCP(com.project.rfidCheck.plc.modbus.CommandConstant.CHECK_UID, 20);
                //[2,3,c8,0,1,0,2,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
                ByteQueue rfidByte = tcpReader.modbusReadWithTCP(com.project.rfidCheck.plc.modbus.CommandConstant.CHECK_RFID, 100);

                String workCell = String.valueOf(cellByte.peek(3, 2)[1]);
                String uid = toString(uidByte.peek(3, 40));
                String rfid = toString(rfidByte.peek(3, 200));

                QueryWrapper<ProduBind> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("bind_state", "1");
                queryWrapper.eq("rfid_uid", uid);
                queryWrapper.eq("work_cell_code", workCell);
                queryWrapper.eq("rfid_data", rfid);

                ProduBind produBind = produBindService.getOne(queryWrapper);
                ProduCheck produCheck = new ProduCheck();
                produCheck.setCheckDate(new Date());
                produCheck.setCheckState(1);
                int checkResult = 0;
                String returnResult = "NG";
                if (produBind != null) {
                    checkResult = 1;
                    returnResult = "OK";
                    produCheck.setProduBindId(produBind.getId());
                }
                produCheck.setCheckRfidData(rfid);
                produCheck.setCheckRfidUid(uid);
                produCheck.setCheckWorkCell(workCell);
                produCheck.setCheckResult(checkResult);
                produCheckService.save(produCheck);
                //写入结果到PLC
                byte[] cc = returnResult.getBytes();
                int v1 = cc[0] >= 0 ? ((int) (cc[0])) : cc[0] + 256;
                int v2 = cc[1] >= 0 ? ((int) (cc[1])) : cc[1] + 256;
                tcpWriter.modbusWriteWithTCP(com.project.rfidCheck.plc.modbus.CommandConstant.CHECK_RESULT, new short[]{(short) (v1 * 256 + v2)});
            }

        } catch (Exception e) {
            message = e.getMessage();
        } finally {
            if (flag != 2) {
                taskLogService.saveLog("校验RFID信息", flag, data, message);
            }
        }
    }

    public static String toString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if (!"0".equals(String.valueOf(byteArray[i]))) {
                if (String.valueOf(byteArray[i]).length() == 1) {
                    hexString.append(String.valueOf(byteArray[i]));
                } else {
                    hexString.append(StringUtils.toString(new byte[]{byteArray[i]}));
                }
            }
        }
        return hexString.toString();
    }
}
