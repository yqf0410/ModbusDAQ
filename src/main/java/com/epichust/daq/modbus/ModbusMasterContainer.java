package com.epichust.daq.modbus;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.IpParameters;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class ModbusMasterContainer
{
    private String ip = CommandConstant.PLC_IP_ADDRESS;
    private int port = CommandConstant.PLC_TCP_PORT;
    private ModbusMaster tcpMaster = null;

    public ModbusMasterContainer()
    {
        initialModbusMaster();
    }

    public ModbusMasterContainer(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
        initialModbusMaster();
    }

    private void initialModbusMaster()
    {
        ModbusFactory modbusFactory = new ModbusFactory();
        //设备ModbusTCP的Ip与端口，如果不设定端口则默认为502
        IpParameters params = new IpParameters();
        params.setHost(ip);
        params.setPort(port);

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
    }


    public ModbusMaster getModbusMaster()
    {
        if(tcpMaster == null)
        {
            ip = CommandConstant.PLC_IP_ADDRESS;
            port = CommandConstant.PLC_TCP_PORT;
            initialModbusMaster();
        }

        return tcpMaster;
    }
}
