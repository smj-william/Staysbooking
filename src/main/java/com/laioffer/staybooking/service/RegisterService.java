package com.laioffer.staybooking.service;

import com.laioffer.staybooking.entity.Authority;
import com.laioffer.staybooking.entity.User;
import com.laioffer.staybooking.entity.UserRole;
import com.laioffer.staybooking.exception.UserAlreadyExistException;
import com.laioffer.staybooking.repository.AuthorityRepository;
import com.laioffer.staybooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class RegisterService {
    //调用repository

    /*@Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;*/ // 最好写个constructor 然后一起Autowired

    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterService(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;

    }

    //把user信息存入数据库
    @Transactional(isolation = Isolation.SERIALIZABLE)
    // Transaction 要存入就一起存入，有error就不存
    // Isolation 的 Serializable 使得两个被add的数据必须同时被加入, 两句都执行成功才能看到效果

    //如果用户已存在，就抛异常
    public void add(User user, UserRole role) throws UserAlreadyExistException {
        ////如果用户已存在，就抛异常
        if(userRepository.existsById(user.getUsername())){
            throw new UserAlreadyExistException("User already exists");
        }
        //但是不想把password存到数据库里，用户通常都用一个密码给多个账户，所以存password的时候加密一下
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);

        //因为repository 继承了JPA，我们直接调用save
        userRepository.save(user);
        authorityRepository.save(new Authority(user.getUsername(), role.name()));



    }
}
