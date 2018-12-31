package com.yashon.chat.mapper;

import com.yashon.chat.pojo.Users;
import com.yashon.chat.utils.MyMapper;
import com.yashon.chat.vo.FriendRequestVO;
import com.yashon.chat.vo.MyFriendsVO;

import java.util.List;


public interface UsersMapperCustom extends MyMapper<Users> {
	
	public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);
	
	public List<MyFriendsVO> queryMyFriends(String userId);
	
	public void batchUpdateMsgSigned(List<String> msgIdList);
	
}