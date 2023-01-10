package com.rkyang.gulimall.product.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rkyang.common.to.SkuReductionTO;
import com.rkyang.common.to.SpuBoundTO;
import com.rkyang.common.to.es.SkuEsTO;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.Query;
import com.rkyang.common.utils.R;
import com.rkyang.gulimall.product.dao.AttrDao;
import com.rkyang.gulimall.product.dao.ProductAttrValueDao;
import com.rkyang.gulimall.product.dao.SpuInfoDao;
import com.rkyang.gulimall.product.dao.SpuInfoDescDao;
import com.rkyang.gulimall.product.entity.*;
import com.rkyang.gulimall.product.feign.CouponFeignService;
import com.rkyang.gulimall.product.feign.SearchFeignService;
import com.rkyang.gulimall.product.feign.WareFeignService;
import com.rkyang.gulimall.product.service.*;
import com.rkyang.gulimall.product.vo.spusave.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescDao spuInfoDescDao;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrDao attrDao;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private ProductAttrValueDao attrValueDao;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and(qw -> qw.like("id", key).or().like("spu_name", key).or().like("spu_description", key));
        }

        String status = (String) params.get("status");
        if (StringUtils.isNotEmpty(status)) {
            queryWrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String catalogId = (String) params.get("catelogId");
        if (StringUtils.isNotEmpty(catalogId) && !"0".equalsIgnoreCase(catalogId)) {
            queryWrapper.eq("catalog_id", catalogId);
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void up(Long spuId) {
        // 1、通过spuId查询sku信息
        List<SkuInfoEntity> skuList = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        // 查询searchType等于1的规格信息
        List<ProductAttrValueEntity> attrValueEntities = attrValueDao.selectSearchAttrBySpuId(spuId.toString());
        List<SkuEsTO.Attrs> attrs = attrValueEntities.stream().map(item -> {
            SkuEsTO.Attrs attr = new SkuEsTO.Attrs();
            BeanUtils.copyProperties(item, attr);
            return attr;
        }).collect(Collectors.toList());

        List<Long> skuIdList = skuList.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        Map<Long, Boolean> hasStockMap = new HashMap<>();
        try {
            R r = wareFeignService.hasSkuStock(skuIdList);
            hasStockMap = (Map<Long, Boolean>) r.get("data");
        } catch (Exception e) {
            log.error("远程调用库存服务查询sku是否有库存出现异常：{}", e);
            e.printStackTrace();
        }

        Map<Long, Boolean> finalHasStockMap = hasStockMap;
        List<SkuEsTO> skuEsTOList = skuList.stream().map(sku -> {
            SkuEsTO esTO = new SkuEsTO();
            BeanUtils.copyProperties(sku, esTO);
            esTO.setSkuPrice(sku.getPrice());
            esTO.setSkuImg(sku.getSkuDefaultImg());
            // 远程库存服务查询是否有库存
            if (finalHasStockMap.size() > 0) {
                esTO.setHasCount(finalHasStockMap.get(sku.getSkuId().toString()));
            } else {
                esTO.setHasCount(true);
            }
            // 热度评分
            esTO.setHotScore(0L);
            // 查询品牌信息
            BrandEntity brand = brandService.getById(sku.getBrandId());
            esTO.setBrandName(brand.getName());
            esTO.setBrandImg(brand.getLogo());
            // 查询分类信息
            CategoryEntity category = categoryService.getById(sku.getCatalogId());
            esTO.setCatalogName(category.getName());
            // 设置规格信息
            esTO.setAttrs(attrs);
            return esTO;
        }).collect(Collectors.toList());

        // 将检索数据发送至elasticsearch
        try {
            R r = searchFeignService.saveProductUp(skuEsTOList);
            if (r.getCode() != 0) {
                log.error("传输es出现异常");
            } else {
                // 修改商品上架状态
                SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
                spuInfoEntity.setId(spuId);
                spuInfoEntity.setPublishStatus(1);
                this.updateById(spuInfoEntity);
            }
        } catch (Exception e) {
            log.error("SKU上架，调用检索远程服务出现异常：{}", e);
            e.printStackTrace();
        }

    }

    @Override
    @Transactional
    // TODO 高级部分完善
    public void saveSpuInfo(SpuSaveVO vo) {
        // 1、保存spu基本信息；pms-spu-info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.baseMapper.insert(spuInfoEntity);

        // 2、保存spu的描述图片；pms-spu-info-desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescDao.insert(spuInfoDescEntity);

        // 3、保存spu的图片集；pms-spu-images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        // 4、保存spu的规格参数；pms-product-attr-value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> valueEntities = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setSpuId(spuInfoEntity.getId());
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrDao.selectById(attr.getAttrId());
            valueEntity.setAttrName(attrEntity.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(valueEntities);

        // 6、保存spu的积分信息；gulimall-sms》sms-spu-bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTO spuBoundTO = new SpuBoundTO();
        BeanUtils.copyProperties(bounds, spuBoundTO);
        spuBoundTO.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTO);
        if (r.getCode() != 0) {
            log.error("积分信息，远程调用服务保存失败");
        }

        // 5、保存当前spu对应的所有sku信息
        List<Skus> skus = vo.getSkus();
        if (CollectionUtils.isNotEmpty(skus)) {
            skus.forEach(sku -> {

                // 获取默认图片
                String defaultImage = "";
                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImage = image.getImgUrl();
                    }
                }

                // 5.1、sku的基本信息；pms-sku-info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImage);
                skuInfoService.save(skuInfoEntity);

                // 5.2、sku的图片信息；pms-sku-images
                List<SkuImagesEntity> skuImagesEntities = sku.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                })
                        // 过滤掉图片路径为空的数据
                        .filter(entity -> StringUtils.isNotEmpty(entity.getImgUrl()))
                        .collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntities);

                // 5.3、sku的销售属性信息；pms-sku-sale-attr-value
                List<Attr> attr = sku.getAttr();
                if (CollectionUtils.isNotEmpty(attr)) {
                    List<SkuSaleAttrValueEntity> saleAttrValueEntities = attr.stream().map(o -> {
                        SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                        BeanUtils.copyProperties(o, skuSaleAttrValueEntity);
                        skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                        return skuSaleAttrValueEntity;
                    }).collect(Collectors.toList());
                    skuSaleAttrValueService.saveBatch(saleAttrValueEntities);
                }

                // 7、sku的优惠、满减等信息；gulimall-sms》sms-sku-ladder等
                SkuReductionTO reductionTO = new SkuReductionTO();
                BeanUtils.copyProperties(sku, reductionTO);
                reductionTO.setSkuId(skuInfoEntity.getSkuId());
                if (reductionTO.getFullCount() > 0 || reductionTO.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
                    // 过滤掉没有优惠、满减信息的数据，存在优惠或满减的数据才处理
                    R r1 = couponFeignService.saveSkuReduction(reductionTO);
                    if (r1.getCode() != 0) {
                        log.error("优惠信息，远程调用服务保存失败");
                    }
                }
            });
        }

    }
}