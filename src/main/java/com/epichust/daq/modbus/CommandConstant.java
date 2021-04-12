package com.epichust.daq.modbus;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class CommandConstant {

    public static Map<Integer, String> commandMap = new HashMap<Integer, String>();
    /**
     * -----PLC交互-----
     **/
    //操作序列号
    public static short operateSerialNumber = (short) new Random().nextInt();

    //默认PLC的IP地址
    public static final String PLC_IP_ADDRESS = "192.168.0.1";
    //默认PLC的端口
    public static final int PLC_TCP_PORT = 503;
    //默认PLC站点ID
    public static final int DEFAULT_SLAVE_ID = 2;

    //PLC触发校验
    public static final int CHECK_FLAG = 0;
    //PLC读取WORKCELL
    public static final int CHECK_WORK_CELL = 1;
    //PLC读取RFIDUID
    public static final int CHECK_UID = 10;
    //PLC读取RFID内容
    public static final int CHECK_RFID = 30;
    //PLC反馈校验结果
    public static final int CHECK_RESULT = 5;


}
