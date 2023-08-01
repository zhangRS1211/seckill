package com.zhang.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wf.captcha.ArithmeticCaptcha;
import com.zhang.seckill.config.AccessLimit;
import com.zhang.seckill.entity.Order;
import com.zhang.seckill.entity.SeckillOrder;
import com.zhang.seckill.entity.User;
import com.zhang.seckill.exception.GlobalException;
import com.zhang.seckill.rabbit.MQSender;
import com.zhang.seckill.service.GoodsService;
import com.zhang.seckill.service.OrderService;
import com.zhang.seckill.service.SeckillOrderService;
import com.zhang.seckill.util.JsonUtil;
import com.zhang.seckill.vo.GoodsVo;
import com.zhang.seckill.vo.RespBean;
import com.zhang.seckill.vo.RespBeanEnum;
import com.zhang.seckill.vo.SeckillMessage;
import io.netty.util.Timeout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/29 20:32
 */
@Slf4j
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillOrderService seckillOrderService;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private DefaultRedisScript<Long> script;
    private Map<Long,Boolean> EmptyStockMap = new HashMap<>();


    /**
     * @Description 验证码
     * @Param user
     * @Param goodsId
     * @Return
     * @Author zcode
     */
    @GetMapping("/captcha")
    public void verifyCode(User user, Long goodsId, HttpServletResponse response){
        if (user == null || goodsId < 0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //设置请求头为输出图片类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码，将结果放入redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha"+user.getId()+":"+goodsId,captcha.text(),300,TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败",e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/{path}/doSeckill" ,method = RequestMethod.POST)
    public RespBean doSeckill(@PathVariable String path, User user, Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        /*GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //1、判断库存是否还有
        if (goods.getStockCount()<1){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //2、判断是否重复抢购
        *//*SeckillOrder seckillOrder = seckillOrderService.getOne(new LambdaQueryWrapper<SeckillOrder>()
                .eq(SeckillOrder::getUserId, user.getId())
                .eq(SeckillOrder::getGoodsId, goodsId));*//*
        String seckillOrderJson = (String) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (!StringUtils.isEmpty(seckillOrderJson)){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        Order order = orderService.seckill(user, goods);
        if (null != order){
            return RespBean.success(order);
        }*/

        // 验证秒杀地址
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        //1、判断是否重复抢购
        String seckillOrderJson = (String) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (!StringUtils.isEmpty(seckillOrderJson)){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //2、内存标记 减少Redis访问
        if (EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        /*//3、预减 库存
        Long stock = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
        if (stock < 0){
            EmptyStockMap.put(goodsId,true);
            redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }*/
        Long stock = (Long) redisTemplate.
                execute(script, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
        if (stock <= 0){
            EmptyStockMap.put(goodsId,true);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //4、请求入队 立即返回排队中
        SeckillMessage message = new SeckillMessage(user,goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(message));
        return RespBean.success(0); //排队中
    }

    /**
     * @Description验证秒杀地址
     * @Param
     * @Return {@link RespBean}
     * @Author zcode
     * @Date
     */
    @AccessLimit(second = 5,maxCount = 5,needLogin = true)
    @ResponseBody
    @GetMapping("/path")
    public RespBean getPath(User user, Long goodsId,String captcha){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        /*ValueOperations valueOperations = redisTemplate.opsForValue();
        //限制访问次数 5秒内访问5次
        String uri = request.getRequestURI();
        Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
        if (count == null){
            valueOperations.set(uri+":"+user.getId(),1,5, TimeUnit.SECONDS);
        }else if (count < 5){
            valueOperations.increment(uri+":"+user.getId());
        }else{
            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
        }*/
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check){
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        String str = orderService.createPath(user, goodsId);
        return RespBean.success(str);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(),false);
        });
    }
    /**
     * @Description 获取秒杀结果
     * @Param user
     * @Param goodsId
     * @Return {@link RespBean} orderId 成功 -1 秒杀失败 0排队中
     * @Author zcode
     * @Date
     */
    @ResponseBody
    @GetMapping("/result")
    public RespBean getResult(User user,Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }


}
