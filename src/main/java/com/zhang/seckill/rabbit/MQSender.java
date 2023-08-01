package com.zhang.seckill.rabbit;

import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author zcode
 * @Data 2023/7/30 21:31
 */
@Service
@Slf4j
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSeckillMessage(String message){
        log.info("发送消息:" + message);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.msg",message);
    }

}
