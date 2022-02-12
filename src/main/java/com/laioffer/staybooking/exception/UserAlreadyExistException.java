package com.laioffer.staybooking.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String message) {
        //调用父类构造函数
        super(message);
    }
}
