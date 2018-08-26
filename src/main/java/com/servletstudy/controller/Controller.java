package com.servletstudy.controller;

import com.servletstudy.annotation.Param;
import com.servletstudy.annotation.TestAutoWired;
import com.servletstudy.annotation.TestController;
import com.servletstudy.annotation.TestRequstMapping;
import com.servletstudy.service.Service;

/**
 * Controller
 *
 * @author xuqie
 * @version 1.0.0
 **/

@TestController
@TestRequstMapping("/testMapping")
public class Controller {

    @TestAutoWired("testService")
    private Service service;

    @TestRequstMapping("/testSay")
    public String testSay(@Param("name")String name,@Param("age") Integer age){
          return service.sayIt(name,age);
    }


    @TestRequstMapping("/testGoodBye")
    public String testGoodBye(@Param("name") String name,@Param("age") Integer age){
        return service.sayIt(name,age);
    }

}
