package com.zhang.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhang.seckill.entity.User;
import com.zhang.seckill.vo.LoginVo;
import com.zhang.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/28 21:39
 */
public interface UserService extends IService<User> {
    //登录
    RespBean login(HttpServletRequest request, HttpServletResponse response,LoginVo loginVo);

    /**
     * @Description  根据cookie获取用户
     * @Param userTicket
     * @Param request
     * @Param response
     * @Return {@link User}
     * @Author zcode
     * @Date
     */
    User getByUserTicket(String userTicket,HttpServletRequest request,HttpServletResponse response);
}
