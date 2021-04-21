package com.epichust.modbusDAQ.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.epichust.modbusDAQ.utils.SystemParameters;
import com.serotonin.io.serial.SerialParameters;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegistersResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
@EnableScheduling
public class RFIDReadPLCService {

    //3.添加定时任务
    //@Scheduled(fixedRate = 1000)
    private void configureTasks() {

        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
        SystemParameters sp = new SystemParameters();
        ModbusMaster master = null;
        if ("com".equals(sp.getType())) {
            SerialParameters serialParameters = new SerialParameters();
            // 设定MODBUS通讯的串行口
            serialParameters.setCommPortId(sp.getCommPortId());
            // 设定成无奇偶校验
            serialParameters.setParity(sp.getParity());
            // 设定成数据位是8位
            serialParameters.setDataBits(sp.getDataBits());
            // 设定为1个停止位
            serialParameters.setStopBits(sp.getStopBits());
            // 设定端口名称
            serialParameters.setPortOwnerName(sp.getPortOwnerName());
            // 设定端口波特率
            serialParameters.setBaudRate(sp.getPort());
            // 创建ModbusFactory工厂实例
            ModbusFactory modbusFactory = new ModbusFactory();
            // 创建ModbusMaster实例
            master = modbusFactory.createRtuMaster(serialParameters);
        } else if ("tcp".equals(sp.getType())) {
            ModbusFactory modbusFactory = new ModbusFactory();
            //设备ModbusTCP的Ip与端口，如果不设定端口则默认为502
            IpParameters params = new IpParameters();
            params.setHost(sp.getIp());
            params.setPort(sp.getPort());
            //参数1：IP和端口信息
            //参数2：保持连接激活
            master = modbusFactory.createTcpMaster(params, true);
            try {
                System.out.println("modbus tcp 连接初始化！");
                master.init();
                System.out.println("modbus tcp 连接成功！");
            } catch (ModbusInitException e) {
                System.out.println("modbus tcp 连接异常!");
            }
        }

        // 初始化
        try {
            Map<String, Map<String, String>> map = new HashMap<>();
            for (Map dbPlcRel : sp.getDbPlcRel()) {
                // READ_HOLDING_REGISTERS = 3
                ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(
                        sp.getSlaveId(), Integer.parseInt(dbPlcRel.get("ps").toString()), Integer.parseInt(dbPlcRel.get("pl").toString()));
                ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master
                        .send(request);
                if (response.isException()) {
                    System.out.println("Exception response: message="
                            + response.getExceptionMessage());
                } else {
                    String data = Arrays.toString(response.getShortData());
                    System.out.println(dbPlcRel.get("tn") + "----" + dbPlcRel.get("cn") + "----" + data);
                    if (map.containsKey(dbPlcRel.get("tn"))) {
                        map.get(dbPlcRel.get("tn")).put(dbPlcRel.get("cn").toString(), data);
                    } else {
                        Map cMap = new HashMap();
                        cMap.put(dbPlcRel.get("cn"), data);
                        map.put(dbPlcRel.get("tn").toString(), cMap);
                    }
                }
            }

            for (String tableName : map.keySet()) {
                String sql = "insert " + tableName;
                String column = "";
                String data = "";
                for (String cn : map.get(tableName).keySet()) {
                    column += cn + ",";
                    data += map.get(tableName).get(cn) + ",";
                }
                sql += column.substring(0, data.length() - 1) + ") value (" + data.substring(0, data.length() - 1) + ")";
                //创建properties对象
                Properties pro = new Properties();
                pro.setProperty("driverClassName", sp.getDriverClassName());
                pro.setProperty("url", sp.getUrl());
                pro.setProperty("username", sp.getUsername());
                pro.setProperty("password", sp.getPassword());
                DataSource ds = DruidDataSourceFactory.createDataSource(pro);
                Connection conn = ds.getConnection();
                conn.prepareStatement(sql).execute();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            master.destroy();
        }
    }
}
