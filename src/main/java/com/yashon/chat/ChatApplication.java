package com.yashon.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName ChatApplication
 * @Description TODO
 * @Author yashon
 * @Date 2018/12/31 下午1:48
 * @Version 1.0
 **/

@SpringBootApplication
@MapperScan(basePackages = "com.yashon.chat.mapper")
@ComponentScan(basePackages = {"com.yashon.chat","org.n3r.idworker"})
public class ChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class,args);
    }
}
