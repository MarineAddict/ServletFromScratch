package com.servletstudy.common.util;

/**
 * StringUtils
 *
 * @author xuqie
 * @version 1.0.0
 **/

public class StringUtils {

    public static String firstLetterToLower(String word){
        if(word==null){
            return null;
        }
        String wd=word.trim();
        if(Character.isLowerCase(wd.charAt(0))){
            return wd;
        }
        return new StringBuilder().append(Character.toLowerCase(wd.charAt(0))).append(wd.substring(1)).toString();
    }

}
