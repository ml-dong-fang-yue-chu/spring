package com.ml.spring.demo.controller;

import com.ml.spring.demo.pojo.User;
import com.ml.spring.demo.service.UserService;
import com.ml.spring.framework.annotation.MLAutoWired;
import com.ml.spring.framework.annotation.MLController;
import com.ml.spring.framework.annotation.MLRequestMapping;
import com.ml.spring.framework.annotation.MLRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UserController
 * @DESC 用户Controller层
 * @Author ML
 * @Date 2019/4/25 19:50
 * @Version 1.0
 */
@MLController
@MLRequestMapping("user")
public class UserController {

    @MLAutoWired("userService")
    private UserService userService;


    @MLRequestMapping("selectById")
    public User selectById(HttpServletRequest request, HttpServletResponse response, @MLRequestParam("id") String id){
       return userService.selectById(id);
    }

    @MLRequestMapping("add")
    public int add(HttpServletRequest request, HttpServletResponse response, @MLRequestParam("id") String id){
        return userService.add(id);
    }



}
