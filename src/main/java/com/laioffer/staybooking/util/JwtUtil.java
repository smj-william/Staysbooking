package com.laioffer.staybooking.util;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Component
public class JwtUtil {
    //@value 是让spring inseart 配置文件里的jwt.secret
    @Value("${jwt.secret}")
    private String secret; //安全密钥 your-256-bit-secret ,JWT key
    //写在配置文件里最好，application.properties

    //加密
    //用dependency里io.jsonwebtoken方式创建
    public String generateToken(String subject){
        //最终返回值其实就是jwt那个网页里例子中的encode部分的密文
        return Jwts.builder()
                .setClaims(new HashMap<>())//有点像jwt里面payload，可以自己修改各种key value pair的数据封装进去
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))//一天有效期，ms是单位
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    //解密

    //反解析token信息
    //private method,用户只关心其他public的方法
    private Claims extractClaims(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        //get body就是jwt网页例子中紫色那部分
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    //验证从payload里面拿出来的有效期是否过期
    public Boolean validateToken(String token) {
        return extractExpiration(token).after(new Date(System.currentTimeMillis()));
        //看过期时间是不是在后面这个new date（当前时间）之后
        //Data(),不传参数也可以，就是constructor出来的就是当前时间
    }
    //如果登陆信息太多，可能还是要用session的方式来做，因为token存的东西不会那么多
    // 用session的好处是，可以检查用户习惯。比如session里存个登陆device，如果发现device变更，可以提醒用户检查是不是本人操作
}
