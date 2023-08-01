package com.zhang.seckill.config;

import com.zhang.seckill.entity.User;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/31 23:32
 */
public class UserContext {

    private static ThreadLocal<User> userHoler = new ThreadLocal<>();

    public static void setUser(User user){
        userHoler.set(user);
    }
    public static User getUser(){
        return userHoler.get();
    }



}
