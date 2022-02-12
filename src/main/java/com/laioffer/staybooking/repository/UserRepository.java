package com.laioffer.staybooking.repository;


import com.laioffer.staybooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//Jpa（java persistance api） 定义了一些常用操作数据库的方法
// 宽泛概念 ORM = Object related mapping
//jpa就是ORM在java里的实现
public interface UserRepository extends JpaRepository<User, String> {

}
