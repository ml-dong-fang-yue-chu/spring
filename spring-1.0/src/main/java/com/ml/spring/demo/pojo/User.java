package com.ml.spring.demo.pojo;

/**
 * @ClassName User
 * @DESC 用户实体类
 * @Author ML
 * @Date 2019/4/25 19:51
 * @Version 1.0
 */
public class User {
    private String id;

    private String username;

    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
