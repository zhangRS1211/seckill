package com.zhang.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhang.seckill.entity.Goods;
import com.zhang.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description 接口
 * @Author zcode
 * @Data 2023/7/29 14:58
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> findGoodsVo();

    /**
     * @Description 根据商品id获取商品详情
     * @Param goodsId
     * @Return {@link GoodsVo}
     * @Author zcode
     * @Date
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);


}
