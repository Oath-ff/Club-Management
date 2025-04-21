package com.project.mapper;


import com.project.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    //根据用户名查找用户
    @Select("select * from users where username=#{username}")
    User findByUserName(String username);

    //添加用户信息
    @Insert("insert into users(username,password,role) values(#{username},#{password},'用户')")
    void add(String username, String password);

    //修改密码
    @Update("update users set password=#{md5String} where user_id = #{userId}")
    void updatePwd(String md5String,Integer userId);

    //修改头像
    @Update("update users set user_pic = #{userPic} where user_id = #{userId}")
    void updateAvatar(String userPic,Integer userId);

    //修改用户权限角色
    @Update("UPDATE users SET username = #{username}, role = #{role}, password = #{password}, user_pic = #{userPic} WHERE username = #{username}")
    void updateUser(User user);

    //更新昵称和联系电话
    @Update("UPDATE users SET nickname = #{nickname}, phone_number = #{phoneNumber} WHERE username = #{username}")
    void updateContactInfo(@Param("username") String username,@Param("nickname") String nickname, @Param("phoneNumber") String phoneNumber);

}
