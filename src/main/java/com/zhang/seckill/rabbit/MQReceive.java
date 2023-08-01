package com.zhang.seckill.rabbit;

import com.zhang.seckill.entity.User;
import com.zhang.seckill.service.GoodsService;
import com.zhang.seckill.service.OrderService;
import com.zhang.seckill.util.JsonUtil;
import com.zhang.seckill.vo.GoodsVo;
import com.zhang.seckill.vo.SeckillMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/30 21:33
 */
@Service
@Slf4j
public class MQReceive {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String msg){
        log.info("QUEUE接受消息:"+msg);
        //将Sender传来的消息对象转成秒杀信息对象
        SeckillMessage message = JsonUtil.jsonStr2Object(msg, SeckillMessage.class);
        Long goodsId = message.getGoodsId();
        User user = message.getUser();
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goods.getStockCount() < 1){
            return;
        }
        //redis中判断是否重复抢购
        String seckillOrderJson = (String) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (!StringUtils.isEmpty(seckillOrderJson)) {
            return;
        }
        orderService.seckill(user,goods);
    }

}
