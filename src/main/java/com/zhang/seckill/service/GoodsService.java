package com.zhang.seckill.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zhang.seckill.entity.Goods;
import com.zhang.seckill.vo.GoodsVo;

import java.util.List;

public interface GoodsService extends IService<Goods> {

    /**
     * @Description 获取商品列表
     * @Param
     * @Return {@link List< GoodsVo>}
     * @Author zcode
     * @Date
     */
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
