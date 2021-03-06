package com.yashon.chat.controller;

import com.yashon.chat.enums.OperatorFriendRequestTypeEnum;
import com.yashon.chat.enums.SearchFriendsStatusEnum;
import com.yashon.chat.pojo.Users;
import com.yashon.chat.service.UserService;
import com.yashon.chat.utils.JSONResult;
import com.yashon.chat.vo.UsersVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping(value = "/users/search")
    public JSONResult searchUser(@RequestParam("myUserId") String myUserId,@RequestParam("friendUsername")  String friendUsername){
        if(StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)){
            return JSONResult.errorMsg("");
        }

        Integer res = userService.preconditionSearchFriends(myUserId, friendUsername);
        if(res == 0){
            //可以添加好友
            Users friendUser = userService.queryUserByUserName(friendUsername);
            return JSONResult.ok(friendUser);
        }else{
            String msg = SearchFriendsStatusEnum.getMsgByKey(res);
            return JSONResult.errorMsg(msg);
        }

    }

    @RequestMapping(value = "/users/addFriend")
    public JSONResult addFriend(@RequestParam("myUserId") String myUserId,@RequestParam("friendUsername")  String friendUsername){
        if(StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)){
            return JSONResult.errorMsg("");
        }

        Integer res = userService.preconditionSearchFriends(myUserId, friendUsername);
        if(res == 0){
            //添加好友
            userService.addFriend(myUserId,friendUsername);
            return JSONResult.ok();
        }else{
            String msg = SearchFriendsStatusEnum.getMsgByKey(res);
            return JSONResult.errorMsg(msg);
        }

    }

    @RequestMapping(value = "/users/queryFriendsRequests")
    public JSONResult queryFriendsRequests(@RequestParam("userId") String userId){

        if(StringUtils.isBlank(userId)){
           return JSONResult.errorMsg("");
        }

        System.out.println(userService.queryFriendRequestList(userId));
        return JSONResult.ok(userService.queryFriendRequestList(userId));
    }

    @RequestMapping(value = "/users/operFriendRequest")
    public JSONResult operFriendRequest(@RequestParam("acceptUserId") String acceptUserId,@RequestParam(
            "sendUserId") String sendUserId,@RequestParam("operType") Integer operType){

        if(StringUtils.isBlank(acceptUserId) || StringUtils.isBlank(sendUserId) || null == operType){
            return JSONResult.errorMsg("");
        }

        if(StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))){
            return JSONResult.errorMsg("");
        }

        if(OperatorFriendRequestTypeEnum.IGNORE.type.intValue() == operType){
            //忽略好友
            userService.delteFriendRequest(sendUserId,acceptUserId);
        }else {
            //通过好友
            userService.passFriendRequest(sendUserId,acceptUserId);
        }

        return JSONResult.ok();
    }

    @RequestMapping(value = "/users/myFriends")
    public JSONResult queryAllFriendsByUserId(@RequestParam("userId") String userId){

        if(StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("");
        }

        return JSONResult.ok(userService.queryAllFriendsByUserId(userId));
    }
}
