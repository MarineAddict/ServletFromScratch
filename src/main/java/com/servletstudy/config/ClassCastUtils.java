package com.servletstudy.config;

/**
 * ClassCastUtils
 * 反射时用到的一些类型转换
 * @author xuqie
 * @version 1.0.0
 **/

public class ClassCastUtils {

    public static Object convert(Class type,String paramValue){

        switch (type.getName()){
            case "java.lang.String" : {
                return paramValue;
            }
            case "java.lang.Integer" :{
                return Integer.parseInt(paramValue);
            }
        }

        return null;
    }
}
