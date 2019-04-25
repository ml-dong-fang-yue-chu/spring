package com.ml.spring.framework.webmvc;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @ClassName MlDispatcherServlet
 * @DESC 核心调度服务类
 * @Author ML
 * @Date 2019/4/25 19:59
 * @Version 1.0
 */
public class MlDispatcherServlet  extends HttpServlet {

    private static final String SCAN_PACKAGE= "scanPackage";

    private final String paramName = "contextConfigLocation";

    private final Properties contextConfig = new Properties();

    //保存扫描到的className
    private final List<String> classNames = new ArrayList<>();



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //加载配置文件
        doLoadConfig(config.getInitParameter(paramName));
        //扫描相关的类
        doScanner((String) contextConfig.get(SCAN_PACKAGE));
        //注册实例到容器中
        doRegistry();
        //DI操作 给字段赋值
        doAutowired();
        //保存url和method的关系
        initHandlerMapping();

    }

    private void initHandlerMapping() {
    }

    private void doAutowired() {

    }

    private void doRegistry() {

    }
    private void doScanner(String  packageName) {
        //拿到classes文件下的路径
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.","/"));
        File file = new File(url.getPath());
        //遍历所有文件，当遍历到还是文件时，则递归
        for (File listFile : file.listFiles()) {
            if(listFile.isDirectory()){
                doScanner(packageName+"."+listFile.getName());
            }else{
                //否则遍历到了class文件，我们将文件名取出，拼接处完成className;
                classNames.add(packageName+"."+listFile.getName().replaceAll(".class",""));
            }
        }



    }

    private void doLoadConfig(String contextConfigLocation) {
        //找到contextConfigLocation下的配置文件信息
        //scanPackage=com.gupaoedu.demo  key/value的形式
        //将配置文件里面的信息保存到Properties容器中
        InputStream is = this.getClass().getClassLoader().
                getResourceAsStream(contextConfigLocation.replaceAll("classpath:",""));

        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
