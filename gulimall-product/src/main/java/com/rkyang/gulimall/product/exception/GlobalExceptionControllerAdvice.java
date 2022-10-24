package com.rkyang.gulimall.product.exception;

import com.rkyang.common.exception.StatusCodeEnum;
import com.rkyang.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理
 * @author rkyang (rkyang@outlook.com)
 * @date 2022/10/24
 */
@Slf4j
@RestControllerAdvice("com.rkyang.gulimall.product.controller")
public class GlobalExceptionControllerAdvice {

    /**
     * controller参数校验异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        log.info("出现了异常：{}，异常类型：{}", e, e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        List<String> resultMsgList = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            resultMsgList.add(fieldError.getDefaultMessage());
        }
        return R.error(StatusCodeEnum.VALID_EXPECTION.getCode(), StatusCodeEnum.VALID_EXPECTION.getMessage()).put("data", resultMsgList);
    }

    public R handleExpection(Throwable throwable) {
        log.info("出现了异常：{}，异常类型：{}", throwable, throwable.getClass());
        return R.error(StatusCodeEnum.UNKNOW_EXPECTION.getCode(), StatusCodeEnum.UNKNOW_EXPECTION.getMessage());
    }
}
