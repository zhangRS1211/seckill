package com.zhang.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhang.seckill.entity.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀商品表 Mapper 接口
 *
 * @author LiChao
 * @since 2022-03-03
 */
@Mapper
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

}
