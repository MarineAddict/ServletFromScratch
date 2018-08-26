package com.servletstudy.config;

import com.servletstudy.service.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * HandleEntity
 * 为了记录对象和方法的一个实体,当handlerMapping根据key找到这个对象，时，可以直接执行自己的invoke方法
 * @author xuqie
 * @version 1.0.0
 **/

public class HandleEntity {
    private Method method;
    private Object clazz;

    public Object invokeMethod(Object[] args){
        if(method==null||clazz==null) return null;
        if (method.getParameterCount()!=args.length) return null; //参数数量必须符合要求
        try {
            return method.invoke(clazz,args);
        }  catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HandleEntity(Method method, Object clazz) {
        this.method = method;
        this.clazz = clazz;
    }

    public Method getMethod() {
        return method;
    }

    public static void main(String[] args){
        try {
        HandleEntity entity=new HandleEntity(Service.class.getDeclaredMethod("sayIt",String.class,Integer.class),Service.class.newInstance());

            entity.method= Service.class.getDeclaredMethod("sayIt",String.class,Integer.class);
            entity.clazz=Service.class.newInstance();
            System.out.println(entity.invokeMethod(new Object[]{"xuqie",3}));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}
