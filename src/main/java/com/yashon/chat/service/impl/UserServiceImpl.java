package com.yashon.chat.service.impl;

import com.yashon.chat.enums.MsgSignFlagEnum;
import com.yashon.chat.enums.SearchFriendsStatusEnum;
import com.yashon.chat.mapper.*;
import com.yashon.chat.netty.ChatMsg;
import com.yashon.chat.pojo.FriendRequest;
import com.yashon.chat.pojo.MyFriends;
import com.yashon.chat.pojo.Users;
import com.yashon.chat.service.UserService;
import com.yashon.chat.utils.MD5Utils;
import com.yashon.chat.vo.FriendRequestVO;
import com.yashon.chat.vo.MyFriendsVO;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author yashon
 * @Date 2018/12/31 下午3:11
 * @Version 1.0
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private MyFriendsMapper myFriendsMapper;
    @Autowired
    private FriendRequestMapper friendRequestMapper;
    @Autowired
    private UsersMapperCustom usersMapperCustom;
    @Autowired
    private ChatMsgMapper chatMsgMapper;
    @Autowired
    private Sid sid;

    @Override
    public boolean queryUserNameIsExist(String userName) {

        Users user = new Users();
        user.setUserName(userName);
        Users users = usersMapper.selectOne(user);
        return users != null;
    }

    @Override
    public Users queryUserForLogin(String userName, String password) throws Exception {

        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userName", userName);
        criteria.andEqualTo("password", MD5Utils.getMD5Str(password));

        Users users = usersMapper.selectOneByExample(example);

        return users;
    }

    @Override
    public Users regist(Users users) throws Exception {

        users.setId(sid.nextShort());
        users.setPassword(MD5Utils.getMD5Str(users.getPassword()));
        users.setFaceImage("");
        users.setFaceImageBig("");
        users.setNickName(users.getUserName());
        users.setQrCode("");

        usersMapper.insert(users);
        return users;
    }

    @Override
    public Users updateUserNickName(Users users) {
        usersMapper.updateByPrimaryKeySelective(users);
        return usersMapper.selectByPrimaryKey(users.getId());
    }

    @Override
    public Integer preconditionSearchFriends(String myUserId, String friendsUserName) {
        boolean b = queryUserNameIsExist(friendsUserName);
        if (!b) {
            //用户不存在
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        }
        Users user = new Users();
        user.setUserName(friendsUserName);
        Users users = usersMapper.selectOne(user);
        if (myUserId.equals(users.getId())) {
            //不能添加自己
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        }

        MyFriends myFriends = new MyFriends();
        myFriends.setMyFriendUserId(users.getId());
        myFriends.setMyUserId(myUserId);
        List<MyFriends> list = myFriendsMapper.select(myFriends);
        if (!list.isEmpty()) {
            //用户已是自己好友不能再添加
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        }
        return SearchFriendsStatusEnum.SUCCESS.status;
    }

    @Override
    public Users queryUserByUserName(String userName) {
        Users user = new Users();
        user.setUserName(userName);
        return usersMapper.selectOne(user);
    }

    @Override
    public void addFriend(String myUserId, String friendsUserName) {

        Users friendUser = queryUserByUserName(friendsUserName);

        //判断是否曾经加过好友
        FriendRequest request = new FriendRequest();
        request.setSendUserId(myUserId);
        request.setAcceptUserId(friendUser.getId());

        FriendRequest friendRequest = friendRequestMapper.selectOne(request);

        if (null == friendRequest) {
            FriendRequest fr = new FriendRequest();
            fr.setSendUserId(myUserId);
            fr.setAcceptUserId(friendUser.getId());
            fr.setId(sid.nextShort());
            fr.setRequestDateTime(new Date());
            friendRequestMapper.insert(fr);
        }
    }

    @Override
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId) {
        return usersMapperCustom.queryFriendRequestList(acceptUserId);
    }

    @Override
    public void delteFriendRequest(String sendUserId, String acceptUserId) {
        FriendRequest fr = new FriendRequest();
        fr.setAcceptUserId(acceptUserId);
        fr.setSendUserId(sendUserId);
        friendRequestMapper.delete(fr);
    }

    @Override
    public void passFriendRequest(String sendUserId, String acceptUserId) {

        saveFriends(sendUserId, acceptUserId);

        saveFriends(acceptUserId, sendUserId);

        //删除好友记录
        delteFriendRequest(sendUserId, acceptUserId);
    }

    @Override
    public List<MyFriendsVO> queryAllFriendsByUserId(String userId) {
        return usersMapperCustom.queryMyFriends(userId);
    }

    @Override
    public String saveMessage(ChatMsg chatMsg) {
        com.yashon.chat.pojo.ChatMsg msg = new com.yashon.chat.pojo.ChatMsg();
        String id = sid.nextShort();
        msg.setId(id);
        msg.setAcceptUserId(chatMsg.getReceiverId());
        msg.setSendUserId(chatMsg.getSenderId());
        msg.setCreateTime(new Date());
        msg.setMsg(chatMsg.getMsg());
        msg.setSignFlag(MsgSignFlagEnum.unsign.type);
        chatMsgMapper.insert(msg);
        return id;
    }

    @Override
    public void updateSignedMsg(List<String> msgIdList) {
        usersMapperCustom.batchUpdateMsgSigned(msgIdList);
    }

    private void saveFriends(String sendUserId, String acceptUserId) {
        MyFriends mf = new MyFriends();
        mf.setId(sid.nextShort());
        mf.setMyUserId(sendUserId);
        mf.setMyFriendUserId(acceptUserId);
        myFriendsMapper.insert(mf);
    }
}
