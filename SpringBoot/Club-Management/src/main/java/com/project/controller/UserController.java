package com.project.controller;

import com.project.pojo.Result;
import com.project.pojo.User;
import com.project.service.UserService;
import com.project.utils.JwtUtil;
import com.project.utils.Md5Util;
import com.project.utils.ThreadLocalUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

@RestController
@RequestMapping("/user")
@Validated//Spring验证框架
public class UserController{

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/register")//注册
    public Result register(@Pattern(regexp = "^[A-Z]{3}\\d{5}$", message = "请输入正确的学号格式") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {
        //正则表达式^[A-Z]{3}\\d{5}$ 前三个字符必须为大写字母，后面必须跟着5个数字（学号格式） ^\\S{5,16}$ 限制5到16位
        //查询用户
        User u = userService.findByUserName(username);
        if (u == null) {
            //没有占用的用户名
            //注册用户
            userService.register(username, password);
            return Result.success();
        } else{
            //如果被占用
            return Result.error("用户名已注册");
        }
    }

    @PostMapping("/login")//登录
    public Result<String> login(@Pattern(regexp = "^(admin|[A-Z]{3}\\d{5})$", message = "请输入正确的学号格式") String username,@Pattern(regexp = "^\\S{5,16}$") String password) {

        //根据用户名查询用户
        User loginUser = userService.findByUserName(username);
        //判断用户是否已经注册
        if(loginUser==null) {
            return Result.error("用户名错误");
        }
        //判断密码是否正确 loginUser对象中的密码已经进行Md5加密 通过Md5Util类中的getMD5String方法将密码转换后与数据库中的密码进行比对
        if(Md5Util.getMD5String(password).equals(loginUser.getPassword())){
            //登录成功，并生成token
            Map<String,Object> claims = new HashMap<>();
            claims.put("user_id", loginUser.getUserId());
            claims.put("username",loginUser.getUsername());
            String token= JwtUtil.genToken(claims);
            //把token存储到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,1, TimeUnit.DAYS);
            return Result.success(token);
        }
        //密码错误
        return Result.error("密码错误");
    }

    @GetMapping("/userInfo")//用户信息
    public Result<User>userInfo(/*@RequestHeader(name="Authorization") String token*/){

        //根据用户名查询用户
//      Map<String,Object> map= JwtUtil.parseToken(token);
//      String username= (String) map.get("username");
        //使用ThreadLocal
        Map<String,Object> map=ThreadLocalUtil.get();
        String username= (String) map.get("username");
        if (username == null) {
            return Result.error("用户信息获取失败，请重新登录！");
        }
        User user= userService.findByUserName(username);
        return Result.success(user);
    }

    @GetMapping("/findUserInfo")//用户信息
    public Result<User>findUserInfo(@RequestParam String username){

        //根据用户名查询用户
        User user= userService.findByUserName(username);
        return Result.success(user);
    }

    @PutMapping("/updateContactInfo")
    public Result updateContactInfo(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String nickname = params.get("nickname");
        String phoneNumber = params.get("phoneNumber");

        if (username == null || username.isEmpty()) {
            return Result.error("用户名不能为空！");
        }

        userService.updateContactInfo(username, nickname, phoneNumber);
        return Result.success();
    }

    @PatchMapping("/updatePwd")//修改密码
    public Result updatePwd(@RequestBody Map<String,String> params,@RequestHeader("Authorization") String token){

        //校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");

        if(!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)){
            return Result.error("修改失败(缺少必要的参数)！");
        }
        //校验新密码格式
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^\\S{5,16}$");
        Matcher matcher = pattern.matcher(newPwd);
        if (!matcher.matches()) {
            return Result.error("新密码格式不正确，应为5到16个非空字符！");
        }
        //校验原密码是否正确
        //调用userService根据用户名查询原密码，在和oldPwd进行比对
        Map<String,Object> map=ThreadLocalUtil.get();
        String username= (String) map.get("username");
        User loginUser = userService.findByUserName(username);
        if(!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码填写不正确！");
        }
        //检验newPwd与rePwd是否一致
        if(!rePwd.equals(newPwd)){
            return Result.error("两次填写的密码不一致！");
        }
        //调用Service层完成修改密码
        userService.updatePwd(newPwd);
        //删除对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }

    @PatchMapping("/updateAvatar")//修改头像
    public Result updateAvatar(@RequestParam @URL String userPic){

        userService.updateAvatar(userPic);
        return Result.success();
    }

    @PostMapping("/logout")//注销
    public Result logout(@RequestHeader("Authorization") String token) {

        //删除对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success("注销成功");
    }

}
