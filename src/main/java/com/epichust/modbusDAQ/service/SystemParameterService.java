package com.epichust.modbusDAQ.service;

import java.util.Map;

public interface SystemParameterService {
    String selectForm();

    void saveParam(Map<String, String> map);
}
