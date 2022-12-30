package com.rkyang.common.constant;

/**
 * 商品服务枚举
 * @author rkyang (rkyang@outlook.com)
 * @date 2022/12/30
 */
public class ProductConstant {

    /**
     * 商品属性类型枚举
     * @author rkyang (rkyang@outlook.com)
     * @date 2022/11/2
     */
    public enum AttrEnum {

        ATTR_TYPE_BASE(1, "基本属性"),
        ATTR_TYPE_SALE(0, "销售属性");

        AttrEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        private Integer code;
        private String msg;

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
