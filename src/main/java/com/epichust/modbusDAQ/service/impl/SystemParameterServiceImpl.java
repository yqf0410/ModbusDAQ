package com.epichust.modbusDAQ.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.epichust.modbusDAQ.service.SystemParameterService;
import com.epichust.modbusDAQ.utils.SystemParameters;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SystemParameterServiceImpl implements SystemParameterService {
    @Override
    public String selectForm() {
        SystemParameters sp = new SystemParameters();
        return sp.getJson();
    }

    @Override
    public void saveParam(Map map) {
        SystemParameters.setJson((String) map.get("formData"));
    }
}
