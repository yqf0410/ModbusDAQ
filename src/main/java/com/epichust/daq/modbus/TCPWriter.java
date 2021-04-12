package com.epichust.daq.modbus;

import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegistersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class TCPWriter
{
    private int slaveId = com.project.rfidCheck.plc.modbus.CommandConstant.DEFAULT_SLAVE_ID;
    @Autowired
    private com.project.rfidCheck.plc.modbus.ModbusMasterContainer modbusMasterContainer;

    public TCPWriter()
    {

    }

    public TCPWriter(int slaveId)
    {
        this.slaveId = slaveId;
    }

    public void modbusWriteWithTCP(int start, short[] values) throws Exception {
        try
        {
            // WRITE_REGISTERS = 16;功能码16
            WriteRegistersRequest request = new WriteRegistersRequest(slaveId, start, values);
            WriteRegistersResponse response = (WriteRegistersResponse) modbusMasterContainer.getModbusMaster().send(request);
            if (response.isException())
            {
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            } else
            {
                System.out.println("**********");
                System.out.println("Write Register Address：" + start + "; values : " + Arrays.toString(values));
                System.out.println("**********");
            }
        } catch (ModbusTransportException e)
        {
            throw new Exception("PLC连接失败！");
        }
    }
}
