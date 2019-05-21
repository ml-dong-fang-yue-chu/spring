package com.ml.spring.framework.webmvc;

import com.ml.spring.framework.annotation.MLAutoWired;
import com.ml.spring.framework.annotation.MLController;
import com.ml.spring.framework.annotation.MLRequestMapping;
import com.ml.spring.framework.annotation.MLService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.*;

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

    //存放实例化的容器
    private final Map<String,Object> beanMaps = new HashMap<String,Object>();
    //存放url和method的容器
    private final Map<String,Method> handleMappings = new HashMap<String,Method>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req,resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        String url = req.getRequestURI();
        resp.setHeader("Content-type", "text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        if(!handleMappings.containsKey(url)){
            try {
                resp.getWriter().write("404 !");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        Method method = handleMappings.get(url);
        String id = req.getParameter("id");
  /*      Class<?>[] parameterTypes =  method.getParameterTypes();
        Object [] params = new Objects[parameterTypes.length];
        for (int x =0;x <parameterTypes.length;x++) {
            Class parameterType  = parameterTypes[x];
            if(parameterType == HttpServletRequest.class ){
                params[x] =req;
                continue;
            }else if(parameterType == HttpServletResponse.class ){
                params[x] =resp;
                continue;
            }else{
                params[x] = id;
            }
        }*/
        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        try {
           Object result = method.invoke(beanMaps.get(beanName),new Object[]{req,resp,id});
            try {
                resp.getWriter().write(result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


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
        //在spring中是处理的url和controller的对应关系，调用方法时，再通过反射去调用当前类中的方法
        if(classNames.isEmpty()){
            return;
        }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if(!clazz.isAnnotationPresent(MLController.class)){
                    continue;
                }
                String baseUrl = "";
                if(clazz.isAnnotationPresent(MLRequestMapping.class)){
                    MLRequestMapping requestMapping = clazz.getAnnotation(MLRequestMapping.class);
                    //根路径
                    baseUrl = requestMapping.value();
                }
                //
                for (Method method : clazz.getMethods()) {
                    //这里只处理公共的方法，遵循oop原则
                   if(!method.isAnnotationPresent(MLRequestMapping.class)){
                       continue;
                   }
                   MLRequestMapping requestMapping = method.getAnnotation(MLRequestMapping.class);
                   String url ="/"+ baseUrl + "/"+requestMapping.value().replaceAll("/+","/");
                   handleMappings.put(url,method);
                   System.out.println("url:"+url+" method:"+method);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void doAutowired() {
        if(beanMaps.isEmpty()){
            return;
        }
        for (Map.Entry<String, Object> stringObjectEntry : beanMaps.entrySet()) {
            //拿到类中所有的字段，包括私有的
           Field[] fields =  stringObjectEntry.getValue().getClass().getDeclaredFields();
           //遍历字段，进行set注入
            for (Field field : fields) {
               //判断字段是否有MlAutowired的注解
                if(!field.isAnnotationPresent(MLAutoWired.class)){
                    continue;
                }
                MLAutoWired autoWired =  field.getAnnotation(MLAutoWired.class);
                //拿到需要注入的来的beanName,待会需要去容器中取
                String beanName = autoWired.value();
                //判断是否是自定义的
                if(StringUtils.isEmpty(beanName)){
                    beanName = field.getType().getName();
                }
                //强制访问
                field.setAccessible(true);
                //字段进行set注入
                try {
                    field.set(stringObjectEntry.getValue(),beanMaps.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void doRegistry() {
        if(classNames.isEmpty()){
            return;
        }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                //这边只解析MLController 和MLService的注解类
                if(clazz.isAnnotationPresent(MLController.class)) {
                    //得到beanName 首字母小写
                    String beanName = toLowerFirstCase(clazz.getSimpleName());
                    if (beanMaps.containsKey(beanName)) {
                        throw new Exception("The" + beanName + "is exists!");
                    }
                    beanMaps.put(beanName, clazz.newInstance());
                }else if(clazz.isAnnotationPresent(MLService.class)){
                    //获取到当前类的注解的value值
                    MLService service =  clazz.getAnnotation(MLService.class);
                    String beanName = service.value();
                    //判断是否自定义beanName
                    if(StringUtils.isEmpty(beanName)){
                        beanName = toLowerFirstCase(clazz.getSimpleName());
                    }
                    if (beanMaps.containsKey(beanName)) {
                        throw new Exception("The" + beanName + "is exists!");
                    }
                    beanMaps.put(beanName, clazz.newInstance());

                }else{
                    continue;
                }
            } catch (Exception e){
                e.printStackTrace();
            }


        }

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

    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
