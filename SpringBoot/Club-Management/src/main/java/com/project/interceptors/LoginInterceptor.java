package com.project.interceptors;


import com.project.pojo.Result;
import com.project.utils.JwtUtil;
import com.project.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception {

        //令牌验证
        String token= request.getHeader("Authorization");
        //验证token
        try {

            //从redis中获取形同的token
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken=operations.get(token);
            if(redisToken==null){
                //token已经失效
                throw new RuntimeException("请重新登录");
            }
            Map<String,Object> claims = JwtUtil.parseToken(token);
            //把业务数据存储到ThreadLocal中
            ThreadLocalUtil.set(claims);
            //放行
            return true;
        } catch (Exception e) {
            //http响应码为401
            response.setStatus(401);
            //不放行
            return false;
        }
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,Object handler,Exception ex) throws Exception{

        //清空ThreadLocal中的数据
        ThreadLocalUtil.remove();
    }

}
