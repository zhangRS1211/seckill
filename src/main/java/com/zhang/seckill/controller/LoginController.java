package com.zhang.seckill.controller;

import com.zhang.seckill.service.UserService;
import com.zhang.seckill.vo.LoginVo;
import com.zhang.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description 登录
 * @Author zcode
 * @Data 2023/7/28 21:09
 */
@Controller
@Slf4j
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    //跳转登录页
    @GetMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @ResponseBody
    @PostMapping("/doLogin")
    public RespBean doLogin(HttpServletRequest request, HttpServletResponse response, LoginVo loginVo){
        log.info("登录接口 {}",loginVo.toString());
        return userService.login(request, response, loginVo);
    }


}
