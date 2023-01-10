package com.rkyang.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 首页二级分类VO
 * @author rkyang (rkyang@outlook.com)
 * @date 2023/1/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catalog2VO {
    private String catalog1Id;
    private String id;
    private String name;
    private List<Catalog3VO> catalog3List;

    /**
     * 首页三级分类VO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Catalog3VO {
        private String catalog2Id;
        private String id;
        private String name;
    }

}
