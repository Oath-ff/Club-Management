package com.project.service.impl;


import com.project.mapper.UserMapper;
import com.project.pojo.User;
import com.project.service.UserService;
import com.project.utils.Md5Util;
import com.project.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public User findByUserName(String username) {

        User u=userMapper.findByUserName(username);
        return u;

    }

    @Override
    public void register(String username, String password) {

        //密码加密 MD5
        String md5String=Md5Util.getMD5String(password);
        //添加到数据库
        userMapper.add(username,md5String);

    }

    @Override
    public void updatePwd(String newPwd) {

        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("user_id");
        userMapper.updatePwd(Md5Util.getMD5String(newPwd),userId);

    }

    @Override
    public void updateAvatar(String userPic) {

        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("user_id");
        userMapper.updateAvatar(userPic, userId);

    }

    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    @Override
    public void updateContactInfo(String username, String nickname, String phoneNumber) {
        userMapper.updateContactInfo(username, nickname, phoneNumber);
    }

}
