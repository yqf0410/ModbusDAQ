package com.epichust.modbusDAQ.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.serotonin.io.serial.SerialParameters;
import com.serotonin.json.JsonArray;
import lombok.Data;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

@Data
public class SystemParameters {

    /**
     * 采集方式
     * TCP、COM
     */
    private String type = "COM";

    /**
     * 网口
     */

    //默认PLC的IP地址
    private String ip = "172.0.0.1";
    //默认PLC的端口
    private int port = 502;
    //默认PLC站点ID
    private int slaveId = 1;

    /**
     * 串口
     */

    // 设定MODBUS通讯的串行口
    private String commPortId = "COM3";
    // 设定成无奇偶校验
    private int parity = 0;
    // 设定成数据位是8位
    private int dataBits = 8;
    // 设定为1个停止位
    private int stopBits = 1;
    // 设定端口名称
    private String portOwnerName = "";
    // 设定端口波特率
    private int baudRate = 9600;


    /**
     * 保存数据库
     */

    //数据库驱动
    private String driverClassName = "com.mysql.jdbc.Driver";
    //连接地址
    private String url = "jdbc:mysql://localhost:3306/one";
    //用户名
    private String username = "";
    //密码
    private String password = "";

    /**
     * 保存信息
     */
    //{表名tn、字段名cn、点位ps、长度pl、类型pt}
    private List<Map> dbPlcRel;


    public SystemParameters() {
        try {
            if(!FileUtil.file("D:/ModbusDAQ/SystemParameters.txt").exists()){
                SystemParameters sp = new SystemParameters();
                setJson(JSONUtil.toJsonStr(sp));
            }
            JSONObject jsonObj = (JSONObject) JSONUtil.readJSON(FileUtil.file("D:/ModbusDAQ/SystemParameters.txt"), CharsetUtil.defaultCharset());
            if (jsonObj.containsKey("type")) {
                this.type = ((String) jsonObj.get("type"));
            }
            if (jsonObj.containsKey("ip")) {
                this.ip = ((String) jsonObj.get("ip"));
            }
            if (jsonObj.containsKey("port")) {
                this.port = Integer.parseInt(jsonObj.get("port").toString());
            }
            if (jsonObj.containsKey("slaveId")) {
                this.slaveId = Integer.parseInt(jsonObj.get("slaveId").toString());
            }
            if (jsonObj.containsKey("commPortId")) {
                this.commPortId = ((String) jsonObj.get("commPortId"));
            }
            if (jsonObj.containsKey("parity")) {
                this.parity = Integer.parseInt(jsonObj.get("parity").toString());
            }
            if (jsonObj.containsKey("dataBits")) {
                this.dataBits = Integer.parseInt(jsonObj.get("dataBits").toString());
            }
            if (jsonObj.containsKey("stopBits")) {
                this.stopBits = Integer.parseInt(jsonObj.get("stopBits").toString());
            }
            if (jsonObj.containsKey("portOwnerName")) {
                this.portOwnerName = ((String) jsonObj.get("portOwnerName"));
            }
            if (jsonObj.containsKey("baudRate")) {
                this.baudRate = Integer.parseInt(jsonObj.get("baudRate").toString());
            }
            if (jsonObj.containsKey("driverClassName")) {
                this.driverClassName = ((String) jsonObj.get("driverClassName"));
            }
            if (jsonObj.containsKey("url")) {
                this.url = ((String) jsonObj.get("url"));
            }
            if (jsonObj.containsKey("username")) {
                this.username = ((String) jsonObj.get("username"));
            }
            if (jsonObj.containsKey("password")) {
                this.password = ((String) jsonObj.get("password"));
            }
            this.dbPlcRel = new ArrayList<>();
            JSONArray jsonArr = JSONUtil.parseArray(jsonObj.get("dbPlcRel"));
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = (JSONObject) jsonArr.get(i);
                this.dbPlcRel.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getJson() {
        StringBuffer sb = new StringBuffer();
        try {
            File file = FileUtil.file("D:/ModbusDAQ/SystemParameters.txt");
            if(!file.exists()){
                SystemParameters sp = new SystemParameters();
                setJson(JSONUtil.toJsonStr(sp));
            }
            FileReader fileReader = new FileReader(file);
            Reader reader = new InputStreamReader(new FileInputStream(file), "utf-8");
            int ch;
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void setJson(String jsonStr) {
        try {
            FileUtil.writeString(jsonStr,"D:/ModbusDAQ/SystemParameters.txt","utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
