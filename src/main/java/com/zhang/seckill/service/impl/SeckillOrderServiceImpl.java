package com.zhang.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.seckill.entity.SeckillOrder;
import com.zhang.seckill.entity.User;
import com.zhang.seckill.mapper.SeckillOrderMapper;
import com.zhang.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 秒杀订单表 服务实现类
 *
 * @author LiChao
 * @since 2022-03-03
 */
@Service
@Primary
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Resource
    private RedisTemplate redisTemplate;


    /**
     * @Description 获取秒杀结果
     * @Param User
     * @Param goodsId
     * @Return {@link Long}
     * @Author zcode
     * @Date
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new LambdaQueryWrapper<SeckillOrder>()
                .eq(SeckillOrder::getUserId, user.getId())
                .eq(SeckillOrder::getGoodsId, goodsId));
        if (null != seckillOrder){
            return seckillOrder.getId();
        }else {
            if (redisTemplate.hasKey("isStockEmpty:"+goodsId)){
                return -1L;
            }
            else {
                return 0L;
            }
        }

    }
}
