package com.zhang.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhang.seckill.entity.Order;
import com.zhang.seckill.entity.User;
import com.zhang.seckill.vo.GoodsVo;
import com.zhang.seckill.vo.OrderDetailVo;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/29 20:12
 */
public interface OrderService extends IService<Order> {

    /**
     * @Description 秒杀
     * @Param user
     * @Param goods
     * @Return {@link Order}
     * @Author zcode
     * @Date
     */
    Order seckill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);

    /**
     * @Description 验证秒杀地址
     * @Param user
     * @Param goodsId
     * @Param path
     * @Return {@link boolean}
     * @Author zcode
     * @Date
     */
    boolean checkPath(User user, Long goodsId, String path);

    /**
     * @Description 生成秒杀地址
     * @Param user
     * @Param goodsId
     * @Return {@link String}
     * @Author zcode
     * @Date
     */
    String createPath(User user, Long goodsId);

    /**
     * @Description 验证 验证码
     * @Param user
     * @Param goodsId
     * @Param captcha
     * @Return {@link boolean}
     * @Author zcode
     * @Date
     */
    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
