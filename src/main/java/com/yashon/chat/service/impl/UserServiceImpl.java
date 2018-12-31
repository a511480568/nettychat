package com.yashon.chat.service.impl;

import com.yashon.chat.mapper.UsersMapper;
import com.yashon.chat.pojo.Users;
import com.yashon.chat.service.UserService;
import com.yashon.chat.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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

        criteria.andEqualTo("userName",userName);
        criteria.andEqualTo("password",MD5Utils.getMD5Str(password));

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
}
