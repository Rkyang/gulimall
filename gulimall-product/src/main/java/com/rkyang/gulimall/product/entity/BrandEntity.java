package com.rkyang.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 * 
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-24 14:56:01
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名参数不能为空")
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "品牌logo参数不能为空")
	@URL(message = "品牌logo参数格式错误")
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@TableLogic(value = "1", delval = "0")
	@NotNull(message = "显示状态参数不能为空")
	@Pattern(regexp = "^[0,1]$", message = "显示状态参数格式错误")
//	@Min(value = 0, message = "显示状态参数格式错误")
//	@Max(value = 1, message = "显示状态参数格式错误")
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank(message = "检索首字母参数不能为空")
	@Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母参数格式错误")
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序参数不能为空")
	@Min(value = 0, message = "排序参数格式错误")
	private Integer sort;

}
