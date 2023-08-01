package com.zhang.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.seckill.entity.Goods;
import com.zhang.seckill.mapper.GoodsMapper;
import com.zhang.seckill.service.GoodsService;
import com.zhang.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/29 15:04
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;


    /**
     * @Description 获取商品列表
     * @Param
     * @Return {@link List< GoodsVo>}
     * @Author zcode
     * @Date
     */
    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    /**
     * @Description 根据商品id获取商品详情
     * @Param goodsId
     * @Return {@link GoodsVo}
     * @Author zcode
     * @Date
     */
    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}
