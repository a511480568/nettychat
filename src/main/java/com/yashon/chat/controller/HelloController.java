package com.yashon.chat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName HelloController
 * @Description TODO
 * @Author yashon
 * @Date 2018/12/31 下午2:04
 * @Version 1.0
 **/
@RestController
public class HelloController {

    @RequestMapping(value = "/hello")
    public String test(){
        return "hello";
    }
}
