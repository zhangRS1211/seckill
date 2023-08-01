package com.zhang.seckill.exception;

import com.zhang.seckill.vo.RespBeanEnum;

/**
 * @Description 全局异常处理
 * @Author zcode
 * @Data 2023/7/29 10:44
 */
public class GlobalException extends RuntimeException {

    private RespBeanEnum respBeanEnum;

    public RespBeanEnum getRespBeanEnum() {
        return respBeanEnum;
    }

    public void setRespBeanEnum(RespBeanEnum respBeanEnum) {
        this.respBeanEnum = respBeanEnum;
    }

    public GlobalException(RespBeanEnum respBeanEnum) {
        this.respBeanEnum = respBeanEnum;
    }
}
