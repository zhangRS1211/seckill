package com.zhang.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhang.seckill.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
