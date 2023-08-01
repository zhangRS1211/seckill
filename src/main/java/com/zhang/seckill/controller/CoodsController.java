package com.zhang.seckill.controller;

import com.zhang.seckill.entity.User;
import com.zhang.seckill.service.GoodsService;
import com.zhang.seckill.service.UserService;
import com.zhang.seckill.vo.DetailVo;
import com.zhang.seckill.vo.GoodsVo;
import com.zhang.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebFault;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/29 11:02
 */
@Controller
@RequestMapping("/goods")
public class CoodsController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    private GoodsService goodsService;

    //跳转到商品列表页
    @ResponseBody
    @GetMapping("/toList")
    public String toLogin(HttpServletRequest request, HttpServletResponse response, Model model, User user)
    {
        //1、静态缓存页面到redis
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //2、redis中获取页面 如果不为空 直接返回页面
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        //1、获取用户信息
        model.addAttribute("user",user);
        //2、获取商品信息
        model.addAttribute("goodsList",goodsService.findGoodsVo());
        //return "goodsList";
        //如果为空 手动渲染 存入redis并返回
        WebContext context = new WebContext(request,response, request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);
        if (!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList",html,60, TimeUnit.SECONDS);
        }
        return html;
    }

    /**
     * @Description 跳转商品详情页
     * @Param model
     * @Param user
     * @Param goodsId
     * @Return {@link String}
     * @Author zcode
     * @Date
     */
    @ResponseBody
    @RequestMapping(value = "/toDetail/{goodsId}")
    public RespBean toDetail(HttpServletRequest request, HttpServletResponse response, Model model, User user,
                           @PathVariable Long goodsId){
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //剩余开始时间
        int remainSeconds = 0;
        //秒杀还未开始
        if (nowDate.before(startDate)){
            remainSeconds = (int)((startDate.getTime()-nowDate.getTime())/1000);
            System.out.println(remainSeconds+"::remainSeconds");
        // 秒杀已结束
        } else if (nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        // 秒杀中
        }else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setGoodsVo(goods);
        detailVo.setUser(user);
        detailVo.setRemainSeconds(remainSeconds);
        detailVo.setSecKillStatus(secKillStatus);
        return RespBean.success(detailVo);

    }
}
