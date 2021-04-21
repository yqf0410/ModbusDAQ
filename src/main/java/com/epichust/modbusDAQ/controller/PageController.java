package com.epichust.modbusDAQ.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <p>WebPage页面模板映射</p>
 *
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String goSystemParamteter() {
        //跳转前先校验默认用户的
        return "systemParamteter.html";
    }
}
