package com.servletstudy;

import com.servletstudy.annotation.*;
import com.servletstudy.common.util.StringUtils;
import com.servletstudy.config.ClassCastUtils;
import com.servletstudy.config.HandleEntity;
import com.servletstudy.controller.Controller;
import javafx.beans.binding.ObjectExpression;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * TestServlet
 *
 * @author xuqie
 * @version 1.0.0
 **/

public class TestServlet extends HttpServlet {


    /*保存全部的class，便于之后进行spring 注入*/
    private List<String> allClassFiles = new ArrayList();

    /*保存全部的bean实例*/
    private Map<String, Object> beansMap = new HashMap<String, Object>();

    private Map<String,HandleEntity> handlerMap=new HashMap<String, HandleEntity>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String contextPath = req.getContextPath();
        String url = req.getRequestURI();
        String handlerStr = url.replaceAll(contextPath, ""); //handlerMapping的key

//        System.out.println(handlerStr);
        HandleEntity handleEntity = handlerMap.get(handlerStr);
       Parameter[] parameters=handleEntity.getMethod().getParameters();
        List<Object> params=new ArrayList<Object>();//记录参数
       if(parameters.length>0){
           for(Parameter parameter:parameters){
              String param= parameter.getAnnotation(Param.class).value();
              Class paramType= parameter.getType();
               String paramValue=req.getParameter(param);
               params.add(ClassCastUtils.convert(paramType,paramValue));
           }
       }

        Object result = handleEntity.invokeMethod(params.toArray());


        //结果转为前台输入
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        out = resp.getWriter();
        out.append(result.toString());
        out.close();
    }

    @Override
    public void init() throws ServletException {
        /**
         * 首先扫描包下的全部类
         */
        scanAllClassFiles("com.servletstudy");


        /**
         * 实例化全部的注解bean
         */
        instanceBeans();


        /**
         * IOC建立
         */
        ioc();

        /**
         * .建立handlerMapping，即对应的requestMapping和对应的方法
         */
        handlerMapping();


    }

    private void handlerMapping() {
        if(beansMap.isEmpty()){
            return;
        }
        for(Map.Entry entry:beansMap.entrySet()){
           Class clazz= entry.getValue().getClass();
           if(clazz.isAnnotationPresent(TestController.class)){
               /*获得RequestMapping注解*/
               String requestMappingRoot="";
               if(clazz.isAnnotationPresent(TestRequstMapping.class)){
                  TestRequstMapping requstMapping= (TestRequstMapping) clazz.getAnnotation(TestRequstMapping.class);
                   requestMappingRoot =requstMapping.value();
               }
               Method[] methods=clazz.getMethods();
               if(methods.length>0){
                   for(Method method:methods){
                      if( method.isAnnotationPresent(TestRequstMapping.class)){
                          TestRequstMapping methodRequestMapping =method.getAnnotation(TestRequstMapping.class);
                          String methodMapping=methodRequestMapping.value();
                          HandleEntity handleEntity=new HandleEntity(method,entry.getValue());
                          handlerMap.put(requestMappingRoot+methodMapping,handleEntity);
                      }
                   }
               }
           }
        }

    }

    private void ioc() {
        if(beansMap.isEmpty()){
            return;
        }
        for(Map.Entry entry: beansMap.entrySet()){
            Field[] fields=entry.getValue().getClass().getDeclaredFields();
            if(fields.length!=0) {
                for (Field field : fields) {
                   field.setAccessible(true);
                   //访问带注解的属性，开始装配
                   if(field.isAnnotationPresent(TestAutoWired.class)){
                       TestAutoWired testAutoWired=field.getAnnotation(TestAutoWired.class);
                       try {
                           field.set(entry.getValue(),beansMap.get(testAutoWired.value().equals("")?field.getType().getSimpleName():testAutoWired.value()));
                       } catch (IllegalAccessException e) {
                           e.printStackTrace();
                       }
                   }
                }
                //这里可以访问一下
                Controller controller= (Controller) beansMap.get("controller");
            }
        }


    }

    private void instanceBeans() {
        if (allClassFiles.isEmpty()) {
            return;
        }
        /*实例化全部beans*/
        for (String className : allClassFiles) {
            try {
                Class clazz = Class.forName(className);
                /*因为只有 controller和service注解需要实例化，因此只做这几个的实例化*/
                if (clazz.isAnnotationPresent(TestController.class)) {
                    TestController testController = (TestController) clazz.getAnnotation(TestController.class);
                    String key = testController.value().equals("") ? StringUtils.firstLetterToLower(className.substring(className.lastIndexOf(".") + 1)) : testController.value();
                    beansMap.put(key, clazz.newInstance());
                } else if (clazz.isAnnotationPresent(TestService.class)) {
                    TestService testService = (TestService) clazz.getAnnotation(TestService.class);
                    String key = testService.value().equals("") ? StringUtils.firstLetterToLower(className.substring(className.lastIndexOf(".") + 1)) : testService.value();
                    beansMap.put(key, clazz.newInstance());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

        }


    }

    private void scanAllClassFiles(String basePackage) {
        try {
            String path = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/")).getPath();
            File files = new File(path);
            File[] fileNames = files.listFiles();
            for (File file : fileNames) {
                if (file.isDirectory()) {
                    scanAllClassFiles( basePackage+"."+ file.getName());
                } else {
                    allClassFiles.add(basePackage + "." + file.getName().replaceAll("\\.class", ""));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
