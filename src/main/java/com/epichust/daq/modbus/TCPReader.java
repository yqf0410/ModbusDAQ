package com.epichust.daq.modbus;

import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.util.queue.ByteQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TCPReader
{
    private int slaveId = CommandConstant.DEFAULT_SLAVE_ID;
    @Autowired
    private ModbusMasterContainer modbusMasterContainer;

    public TCPReader()
    {
    }

    public TCPReader(int slaveId)
    {
        this.slaveId = slaveId;
    }

    public ByteQueue modbusReadWithTCP(int startPosition, int readLength) throws Exception {
        ByteQueue byteQueue = new ByteQueue(120);
        try
        {
            // READ_HOLDING_REGISTERS = 3;功能码03
            ReadHoldingRegistersRequest modbusRequest = new ReadHoldingRegistersRequest(slaveId, startPosition, readLength);
            ReadHoldingRegistersResponse modbusResponse = (ReadHoldingRegistersResponse) modbusMasterContainer.getModbusMaster().send(modbusRequest);

            if (modbusResponse.isException())
            {
                throw new Exception("Exception response: message=" + modbusResponse.getExceptionMessage());
            } else
            {
                modbusResponse.write(byteQueue);
                System.out.println("----------");
                System.out.println("Read Register Address：" + startPosition + "; length : " + readLength);
                System.out.println("收到的响应信息值:" + byteQueue);
                System.out.println("----------");
            }

        } catch (ModbusTransportException e)
        {
            throw new Exception("PLC连接失败！");
        }
        return byteQueue;
    }

}
