package com.yashon.chat.service;

import com.yashon.chat.netty.ChatMsg;
import com.yashon.chat.pojo.Users;
import com.yashon.chat.vo.FriendRequestVO;
import com.yashon.chat.vo.MyFriendsVO;

import java.util.List;

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

    List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    /**
     * 删除好友请求记录
     * @param sendUserId
     * @param acceptUserId
     */
    void delteFriendRequest(String sendUserId,String acceptUserId);

    void passFriendRequest(String sendUserId,String acceptUserId);

    List<MyFriendsVO> queryAllFriendsByUserId(String userId);

    String saveMessage(ChatMsg chatMsg);

    void updateSignedMsg(List<String> msgIdList);
}
