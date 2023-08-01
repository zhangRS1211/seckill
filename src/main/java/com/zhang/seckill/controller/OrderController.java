package com.zhang.seckill.controller;

import com.zhang.seckill.entity.User;
import com.zhang.seckill.service.OrderService;
import com.zhang.seckill.vo.OrderDetailVo;
import com.zhang.seckill.vo.RespBean;
import com.zhang.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/30 16:34
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @ResponseBody
    @RequestMapping("/detail")
    public RespBean detail(User user,Long orderId){
        if (null == user){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detail = orderService.detail(orderId);
        return RespBean.success(detail);
    }

}
