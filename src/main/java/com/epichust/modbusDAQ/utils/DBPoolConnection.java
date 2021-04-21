package com.epichust.modbusDAQ.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;


public class DBPoolConnection {

    private static DBPoolConnection dbPoolConnection = null;
    private static DruidDataSource druidDataSource = null;


    /**
     * 数据库连接池单例
     *
     * @return
     */
    public static synchronized DBPoolConnection getInstance(String url, String username, String password) {
        Properties properties = new Properties();
        properties.setProperty("url", url);
        properties.setProperty("username", username);
        properties.setProperty("password", password);

        try {
            druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbPoolConnection = new DBPoolConnection();
        return dbPoolConnection;
    }

}