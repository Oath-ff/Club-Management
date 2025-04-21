package com.project;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTTest {

    @Test
    public void testGem(){

        Map<String,Object> claims = new HashMap<>();
        claims.put("id",1);
        claims.put("username","SWE20032");
        //生成JWT
        String token=JWT.create()
                .withClaim("user",claims)//添加载荷
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60*12))//添加过期时间 12小时
                .sign(Algorithm.HMAC256("ACH20032"));//指定算法，配置密钥"ACH20032"

        System.out.println(token);
    }
    @Test
    public void testParse(){

        //定义字符串，模拟用户传递过来的token
        String token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"+
        ".eyJ1c2VyIjp7ImlkIjoxLCJ1c2VybmFtZSI6IlNXRTIwMDMyIn0sImV4cCI6MTczMDY3MjQ4NX0"+
                ".x2zNFGmxQl1ce1e0f3FdqQi_iDL_QWt5009S8hME9Mk";

        JWTVerifier jwtVerifier= JWT.require(Algorithm.HMAC256("ACH20032")).build();//生成JWTVerifier验证器

        DecodedJWT decodedJWT = jwtVerifier.verify(token);//验证token，并生成一个解析后的JWT对象
        Map<String,Claim>claims = decodedJWT.getClaims();
        System.out.println(claims.get("user"));//如果解析出来与上面信息一致说明成功
    }

}
