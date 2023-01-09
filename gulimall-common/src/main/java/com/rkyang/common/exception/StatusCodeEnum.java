package com.rkyang.common.exception;

/**
 * 请求状态码枚举
 * @author rkyang (rkyang@outlook.com)
 * @date 2022/10/24
 */
public enum StatusCodeEnum {

    UNKNOW_EXPECTION(10000, "未知异常"),
    VALID_EXPECTION(10001, "参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架传输ES异常");

    private int code;
    private String message;

    StatusCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
