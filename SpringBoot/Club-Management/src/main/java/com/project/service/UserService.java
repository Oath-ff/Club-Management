package com.project.service;


import com.project.pojo.User;


public interface UserService {

    //根据用户名查找用户
    User findByUserName(String username);
    //注册用户
    void register(String username, String password);

    //修改密码
    void updatePwd(String newPwd);

    //更新头像
    void updateAvatar(String userPic);

    //修改用户权限角色
    void updateUser(User user);

    //修改昵称和联系电话
    void updateContactInfo(String username, String nickname, String phoneNumber);
    //更新昵称和联系电话


}
