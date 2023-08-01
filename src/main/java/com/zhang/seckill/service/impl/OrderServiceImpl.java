package com.zhang.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.seckill.entity.Order;
import com.zhang.seckill.entity.SeckillGoods;
import com.zhang.seckill.entity.SeckillOrder;
import com.zhang.seckill.entity.User;
import com.zhang.seckill.exception.GlobalException;
import com.zhang.seckill.mapper.OrderMapper;
import com.zhang.seckill.service.GoodsService;
import com.zhang.seckill.service.OrderService;
import com.zhang.seckill.service.SeckillGoodsService;
import com.zhang.seckill.service.SeckillOrderService;
import com.zhang.seckill.util.JsonUtil;
import com.zhang.seckill.util.MD5Util;
import com.zhang.seckill.util.UUIDUtil;
import com.zhang.seckill.vo.GoodsVo;
import com.zhang.seckill.vo.OrderDetailVo;
import com.zhang.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sun.security.provider.MD5;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/29 20:13
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @Description 秒杀
     * @Param user
     * @Param goods
     * @Return {@link Order}
     * @Author zcode
     * @Date
     */
    @Override
    @Transactional
    public Order seckill(User user, GoodsVo goods) {
        //1、秒杀商品减库存 根据商品ID获取秒杀商品
        SeckillGoods seckillGoods = seckillGoodsService
                .getOne(new LambdaQueryWrapper<SeckillGoods>()
                        .eq(SeckillGoods::getGoodsId, goods.getId()));
        //2、减少秒杀商品的库存数量
        seckillGoods.setStockCount(seckillGoods.getStockCount() -1);
        //3、更新数据库中秒杀商品的库存数量
        boolean seckKillGoodsResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = "+"stock_count - 1")
                .eq("goods_id", goods.getId())
                .gt("stock_count", 0));
        //seckillGoodsService.updateById(seckillGoods);
        if (seckillGoods.getStockCount() < 1){
            //判断是否还有库存
            redisTemplate.opsForValue().set("isStockEmpty:"+goods.getId(),"o");
            return null;
        }
        //2、生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);

        //将秒杀订单信息存入redis 方便判断是否重复抢购时进行查询
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goods.getId(), JsonUtil.object2JsonStr(seckillOrder));
        return order;
    }

    /**
     * @Description  订单详情
     * @Param orderId
     * @Return {@link OrderDetailVo}
     * @Author zcode
     * @Date
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if(null == orderId){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail = new OrderDetailVo();
        detail.setGoodsVo(goodsVo);
        detail.setOrder(order);
        return detail;
    }

    /**
     * @Description 验证请求地址
     * @Param user
     * @Param goodsId
     * @Param path
     * @Return {@link boolean}
     * @Author zcode
     * @Date
     */
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || StringUtils.isEmpty(path)){
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    /**
     * @Description 生成秒杀地址
     * @Param user
     * @Param goodsId
     * @Return {@link String}
     * @Author zcode
     * @Date
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,str,60, TimeUnit.SECONDS);
        return str;
    }

    /**
     * @Description 校验验证码
     * @Param user
     * @Param goodsId
     * @Param captcha
     * @Return {@link boolean}
     * @Author zcode
     * @Date
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (StringUtils.isEmpty(captcha) || null == user || goodsId < 0){
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return redisCaptcha.equals(captcha);
    }
}
