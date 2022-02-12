package com.laioffer.staybooking.service;

import com.laioffer.staybooking.entity.Token;
import com.laioffer.staybooking.entity.User;
import com.laioffer.staybooking.entity.UserRole;
import com.laioffer.staybooking.entity.Authority;
import com.laioffer.staybooking.exception.UserNotExistException;
import com.laioffer.staybooking.util.JwtUtil;
import org.springframework.stereotype.Service;
import com.laioffer.staybooking.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;


//通过AuthenticationController 来调用
@Service
public class AuthenticationService {
    private AuthenticationManager authenticationManager;
    private AuthorityRepository authorityRepository;
    private JwtUtil jwtUtil;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, AuthorityRepository authorityRepository, JwtUtil jwtUtil) {
        //加了autowired下面这些private必须是bean，spring才能帮助注入
        this.authenticationManager = authenticationManager;
        this.authorityRepository = authorityRepository;

        //是个class，用@component变成个bean
        this.jwtUtil = jwtUtil;
    }

    public Token authenticate(User user, UserRole role) throws UserNotExistException {
        try {
            //通过authenticate这个方法看前端传进来的用户是否存在
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        } catch (AuthenticationException exception) {
            throw new UserNotExistException("User Doesn't Exist");
        }

        //判断用户身份是什么，是host还是guest
        Authority authority = authorityRepository.findById(user.getUsername()).orElse(null);//能找到role就找，找不到就null
        if (!authority.getAuthority().equals(role.name())) {
            throw new UserNotExistException("User Doesn't Exist");
        }

        //如果没问题，就generate token了
        return new Token(jwtUtil.generateToken(user.getUsername()));
    }
    //check user 是否存在 -> generate token -> return token
    //check user -> 有exception -> return not exist exception if no



}
