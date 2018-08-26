package com.servletstudy.service;

import com.servletstudy.annotation.TestService;

/**
 * Service
 *
 * @author xuqie
 * @version 1.0.0
 **/

@TestService("testService")
public class Service {


    public String sayIt(String name,Integer age){
        return "Hello, "+name+", you age is "+ age;
    }



}
