package com.zhang.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.seckill.entity.User;
import com.zhang.seckill.exception.GlobalException;
import com.zhang.seckill.mapper.UserMapper;
import com.zhang.seckill.service.UserService;
import com.zhang.seckill.util.*;
import com.zhang.seckill.vo.LoginVo;
import com.zhang.seckill.vo.RespBean;
import com.zhang.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/28 21:40
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    /*
     * @Description 登录
     * @Param null
     * @Return {@link null}
     * @Author zcode
     * @Date
     */
    @Override
    public RespBean login(HttpServletRequest request, HttpServletResponse response, LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //1、判断用户名或者密码不能为空
        if (StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password))
        {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //2、校验手机号
        if (!ValidatorUtil.isMobile(mobile)){
            return  RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }
        //3、根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if (user == null){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //4、校验密码
        if (!MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        //5、生成cookie
        String ticket = UUIDUtil.uuid();
        /*request.getSession().setAttribute(ticket,user);
        CookieUtil.setCookie(request,response,"userTicket",ticket);*/

        //5.1 修改为redis存储
        //将用户信息存储到redis中
        redisTemplate.opsForValue().set("user:"+ticket, JsonUtil.object2JsonStr(user));
        CookieUtil.setCookie(request,response,"userTicket",ticket);
        return RespBean.success(ticket);
    }

    /**
     * @Description  根据cookie获取用户
     * @Param userTicket
     * @Param request
     * @Param response
     * @Return {@link User}
     * @Author zcode
     * @Date
     */
    @Override
    public User getByUserTicket(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        //1、如果用户为空 则返回null
        if (StringUtils.isEmpty(userTicket)){
            return null;
        }
        //2、在redis中获取用户信息
        String userJson = (String) redisTemplate.opsForValue().get("user:" + userTicket);
        User user = JsonUtil.jsonStr2Object(userJson, User.class);
        if (null != user){
            CookieUtil.setCookie(request,response,"userTicket",userTicket);
        }
        return user;
    }
}
