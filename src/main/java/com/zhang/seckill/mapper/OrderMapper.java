package com.zhang.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhang.seckill.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
