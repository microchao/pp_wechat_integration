package com.iclp.pp.wechatlogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoginTest  {
/*
    @RequestMapping("/")
    @ResponseBody
    String home() {
//        System.out.println(code);
        return "Hello World!";
    }*/

    public static void main(String[] args) throws Exception {
        System.out.println();
        SpringApplication.run(LoginTest.class, args);
    }
}
