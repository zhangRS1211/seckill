package com.zhang.seckill.controller;

import com.zhang.seckill.entity.User;
import com.zhang.seckill.rabbit.MQSender;
import com.zhang.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/29 21:29
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;

    /**
     * @Description 用户信息(测试)
     * @Param user
     * @Return {@link RespBean}
     * @Author zcode
     * @Date
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }

    /*@RequestMapping("/mq/fanout")
    @ResponseBody
    public void mq(){
        mqSender.send("Hello");
    }*/
    /*@RequestMapping("/mq/fanout")
    @ResponseBody
    public void mq(){
        mqSender.send("Hello");
    }*/

}
