package com.epichust.modbusDAQ.modbus.test;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegistersResponse;
import com.serotonin.util.queue.ByteQueue;

public class TCPModbusTest
{
    public static void modbusWriteWithTCP(String ip, int port, int slaveId, int start, short[] values) {
        ModbusFactory modbusFactory = new ModbusFactory();
        //设备ModbusTCP的Ip与端口，如果不设定端口则默认为502
        IpParameters params = new IpParameters();
        params.setHost(ip);
        if (502 != port)
        {
            params.setPort(port);
        }

        ModbusMaster tcpMaster = null;
        //参数1：IP和端口信息
        //参数2：保持连接激活
        tcpMaster = modbusFactory.createTcpMaster(params, true);
        try
        {
            System.out.println("modbus tcp 连接初始化！" );
            tcpMaster.init();
            System.out.println("modbus tcp 连接成功！" );
        }
        catch (ModbusInitException e)
        {
            System.out.println("modbus tcp 连接异常!");
            // TODO
        }
        try
        {
            //WRITE_REGISTERS = 16;
            WriteRegistersRequest request = new WriteRegistersRequest(slaveId, start, values);
            WriteRegistersResponse response = (WriteRegistersResponse) tcpMaster.send(request);
            if (response.isException())
            {
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            }
            else
            {
                System.out.println("Write Success.");
            }
        } catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }

    public static ByteQueue modbusReadWithTCP(String ip, int port, int slaveId, int start,int readLenth) {
        ModbusFactory modbusFactory = new ModbusFactory();
        //设备ModbusTCP的Ip与端口，如果不设定端口则默认为502
        IpParameters params = new IpParameters();
        params.setHost(ip);
        if(502!=port)
        {
            params.setPort(port);
        }

        ModbusMaster tcpMaster = null;
        //参数1：IP和端口信息
        //参数2：保持连接激活
        tcpMaster = modbusFactory.createTcpMaster(params, true);
        try
        {
            System.out.println("modbus tcp 连接初始化！" );
            tcpMaster.init();
            System.out.println("modbus tcp 连接成功！" );
        }
        catch (ModbusInitException e)
        {
            System.out.println("modbus tcp 连接异常!");
            // TODO
        }

        ByteQueue byteQueue= new ByteQueue(12);
        try
        {
            //READ_HOLDING_REGISTERS = 3;
            ReadHoldingRegistersRequest modbusRequest = new ReadHoldingRegistersRequest(slaveId, start, readLenth);//功能码03
            ReadHoldingRegistersResponse modbusResponse = (ReadHoldingRegistersResponse)tcpMaster.send(modbusRequest);


            if (modbusResponse.isException())
            {
                System.out.println("Exception response: message=" + modbusResponse.getExceptionMessage());
            }
            else
            {
                System.out.println("Read Success.");
                modbusResponse.write(byteQueue);
                System.out.println("收到的响应信息大小:"+byteQueue.size());
                System.out.println("收到的响应信息值:"+byteQueue);
            }


        } catch (ModbusTransportException e)
        {
            e.printStackTrace();
        }
        return byteQueue;
    }


    public static void main(String[] args)
    {
        TCPModbusTest.modbusReadWithTCP("192.168.1.8", 502, 1, 500, 4);
        TCPModbusTest.modbusWriteWithTCP("192.168.1.8", 502, 1, 500, new short[]{2, 4, 6, 8});
    }
}