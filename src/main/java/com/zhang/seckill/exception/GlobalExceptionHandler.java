package com.zhang.seckill.exception;

import com.zhang.seckill.vo.RespBean;
import com.zhang.seckill.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description 全局异常处理
 * @Author zcode
 * @Data 2023/7/29 10:45
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
        @ExceptionHandler(Exception.class)
        public RespBean ExceptionHandler(Exception e) {
            if (e instanceof GlobalException) {
                GlobalException exception = (GlobalException) e;
                return RespBean.error(exception.getRespBeanEnum());
            } else if (e instanceof BindException) {
                BindException bindException = (BindException) e;
                RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
                respBean.setMessage("参数校验异常：" + bindException.getBindingResult().getAllErrors().get(0).getDefaultMessage());
                return respBean;
            }
            System.out.println("异常信息" + e);
            return RespBean.error(RespBeanEnum.ERROR);
        }

}
