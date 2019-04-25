package com.ml.spring.demo.service;

import com.ml.spring.demo.pojo.User;

/**
 * @ClassName UserService
 * @DESC 用户抽象接口类
 * @Author ML
 * @Date 2019/4/25 19:52
 * @Version 1.0
 */
public interface UserService {

    User selectById(String id);
}
