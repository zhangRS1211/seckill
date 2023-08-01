package com.zhang.seckill.vo;

import com.zhang.seckill.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/30 16:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {

    private Order order;
    private GoodsVo goodsVo;
}
