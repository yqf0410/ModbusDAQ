package com.epichust.modbusDAQ.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.epichust.modbusDAQ.service.SystemParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/sp")
public class SystemParameterController {

    @Autowired
    private SystemParameterService systemParameterService;

    @PostMapping("/selectParam")
    public String selectParam() {
        return systemParameterService.selectForm();
    }

    @PostMapping("/saveParam")
    public String saveParam(@RequestParam Map<String, String> map) {
        systemParameterService.saveParam(map);
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("data","保存成功！");
        return JSONUtils.toJSONString(returnMap);
    }

}
