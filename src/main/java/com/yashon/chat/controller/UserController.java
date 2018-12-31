package com.yashon.chat.controller;

import com.yashon.chat.pojo.Users;
import com.yashon.chat.service.UserService;
import com.yashon.chat.utils.JSONResult;
import com.yashon.chat.vo.UsersVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author yashon
 * @Date 2018/12/31 下午3:13
 * @Version 1.0
 **/

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/users/registerorlogin")
    public JSONResult loginOrRegister(@RequestBody Users users) throws Exception {

        if(StringUtils.isBlank(users.getUserName()) || StringUtils.isBlank(users.getPassword())){
            return JSONResult.errorMsg("用户名或密码不能为空");
        }

        boolean result = userService.queryUserNameIsExist(users.getUserName());
        Users userResult = null;
        if(result){
            //登陆
            userResult = userService.queryUserForLogin(users.getUserName(),users.getPassword());
            if(null == userResult){
                return JSONResult.errorMsg("用户名或密码错误");
            }
        }else{
            //注册
            userResult = userService.regist(users);
        }

        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(userResult,usersVo);

        return JSONResult.ok(usersVo);
    }

    @RequestMapping(value = "/users/update/nickname")
    public JSONResult updateUserNickName(@RequestBody Users users){

        Users newUser = userService.updateUserNickName(users);
        return JSONResult.ok(newUser);
    }
}
