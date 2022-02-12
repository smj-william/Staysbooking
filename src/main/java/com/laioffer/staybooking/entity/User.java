package com.laioffer.staybooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "user")
@JsonDeserialize(builder = User.Builder.class)
//反序列化;
// 序列化 java obj --> Json
// 反序列化 Json --> java obj （前端的json数据，变成user 对象）
// (builder = User.Builder.class) 告诉他返回回来的时候用builder这种方式创建user对象
public class User implements Serializable { //要变成hibernate table，要加serializable
    private static final long serialVersionUID = 1L;

    @Id
    private String username;

    // user obj --> json时候，隐去这个数据
    @JsonIgnore //user的stay信息，可能会返回到前端显示，不想显示password。json数据里面也不显示
    private String password;

    @JsonIgnore
    private boolean enabled;

    //需要这个构造函数，万一用户写 User a = new User();
    public User() {} //Hibernate 也需要使用，在调用数据库的时候返回成一个一个obj

    //不需要了，直接让用户用17行的那个构造函数
    /*public User(String username, String password, boolean enabled) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }*/

    private User(Builder builder){
        this.username = builder.username;
        this.password = builder.password;
        this.enabled = builder.enabled;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public User setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    //in order to use builder pattern, we need to create this static inner class
    // 为什么要用static：先有builder才有user对象
    public static class Builder{

        // JsonProperty配合 @JsonDeserialize 使用
        // json --> java obj 时候json格式的数据如何与后端对应
        // json格式，postman里面是这么写的：
        //    { "username": "vincent",
        //      "password": "1234" }
        @JsonProperty("username")
        private String username;

        @JsonProperty("password")
        private String password;

        @JsonProperty("enabled")
        private boolean enabled;

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public User build(){
            return new User(this);
        }
    }
}

// User user = new User("vvv", "1234", true)

// Builder builder = new Builder();
// builder.setName("vvv");
// builder.setPassword("1234");
// builder.setEnabled(true);

// User user = new User(builder); // private private User(Builder builder){} 了，这行用不了

// User user = builder.build();

// return this 好处 --> builder.setName("vvv").setPassword("1234").setEnabled(true);

// 当private field多的时候，可能存在optional的field，就用builder pattern 比较方便，用户想set哪个就哪个