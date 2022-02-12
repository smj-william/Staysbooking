package com.laioffer.staybooking.filter;

import com.laioffer.staybooking.entity.Authority;
import com.laioffer.staybooking.repository.AuthorityRepository;
import com.laioffer.staybooking.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//此class是为了看用户是否登陆，登陆了才能给权限上传，delete stay
//check

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final String HEADER = "Authorization"; //token
    private final String PREFIX = "Bearer"; //前端token的 prefix: prefix + token

    private JwtUtil jwtUtil; //为了解析token
    private AuthorityRepository authorityRepository;
    @Autowired
    public JwtFilter(JwtUtil jwtUtil, AuthorityRepository authorityRepository) {
        this.jwtUtil = jwtUtil;
        this.authorityRepository = authorityRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
    //FilterChain： 可能多个filter，不一定只验证token

        //filter by token xxx --> other filter

        //JwtUtil 里我们写好了解析token的方法

        //通过request得到header
        String authorizationHeader = httpServletRequest.getHeader(HEADER);
        String jwt = null; //token
        if(authorizationHeader != null && authorizationHeader.startsWith(PREFIX)){
            jwt = authorizationHeader.substring(7);
        }


        //Sercurity context holder 来记录从token解析后的user：https://www.baeldung.com/get-user-in-spring-security
        // token validata --> user(sub) --> 存入security context holder
        // 在需要用到的controller (传惨Principle ) --> spring会去之前context holder 里把这个user对应拿出来
        if (jwt != null && jwtUtil.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
            // valid token
            String username = jwtUtil.extractUsername(jwt);//很多操作都需要username，判断是谁
            Authority authority = authorityRepository.findById(username).orElse(null);
            if (authority != null) {
                List<GrantedAuthority> grantedAuthorities = Arrays.asList(new GrantedAuthority[]{new SimpleGrantedAuthority(authority.getAuthority())});
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, grantedAuthorities);
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }


        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }
}
