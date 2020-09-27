package com.offcn.sellergoods.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * @Author dsy
     * @Date 2020/9/4 11:05
     * @Description 获取用户名
     * @Return java.lang.String
     * @Since version-1.0
     */

    @RequestMapping("/name")
    public Map name(){
//        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        HashMap<String, String> map = new HashMap<>();
        map.put("loginName","");
        return map;
    }

}
