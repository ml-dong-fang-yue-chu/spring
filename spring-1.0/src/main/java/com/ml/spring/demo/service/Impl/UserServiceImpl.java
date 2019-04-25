package com.ml.spring.demo.service.Impl;

import com.ml.spring.demo.pojo.User;
import com.ml.spring.demo.service.UserService;

/**
 * @ClassName UserServiceImpl
 * @DESC 用户业务实现类
 * @Author ML
 * @Date 2019/4/25 19:53
 * @Version 1.0
 */
public class UserServiceImpl implements UserService {

    @Override
    public User selectById(String id) {
        User user = new User();
        user.setId(id);
        user.setUsername("张三");
        user.setPassword("11234456");
        return user;
    }
}
