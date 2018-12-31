package com.yashon.chat.service;

import com.yashon.chat.pojo.Users;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author yashon
 * @Date 2018/12/31 下午3:11
 * @Version 1.0
 **/
public interface UserService {

    boolean queryUserNameIsExist(String userName);

    Users queryUserForLogin(String userName,String password) throws Exception;

    Users regist(Users users) throws Exception;

    Users updateUserNickName(Users users);

    /**
     * 搜索用户
     * @param myUserId
     * @param friendsUserName
     * @return
     */
    Integer preconditionSearchFriends(String myUserId,String friendsUserName);

    Users queryUserByUserName(String userName);

    void addFriend(String myUserId,String friendsUserName);
}
