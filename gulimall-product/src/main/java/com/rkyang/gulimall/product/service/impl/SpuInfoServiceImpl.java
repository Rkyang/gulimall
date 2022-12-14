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
        // 1?????????spuId??????sku??????
        List<SkuInfoEntity> skuList = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        // ??????searchType??????1???????????????
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
            log.error("??????????????????????????????sku??????????????????????????????{}", e);
            e.printStackTrace();
        }

        Map<Long, Boolean> finalHasStockMap = hasStockMap;
        List<SkuEsTO> skuEsTOList = skuList.stream().map(sku -> {
            SkuEsTO esTO = new SkuEsTO();
            BeanUtils.copyProperties(sku, esTO);
            esTO.setSkuPrice(sku.getPrice());
            esTO.setSkuImg(sku.getSkuDefaultImg());
            // ???????????????????????????????????????
            if (finalHasStockMap.size() > 0) {
                esTO.setHasCount(finalHasStockMap.get(sku.getSkuId().toString()));
            } else {
                esTO.setHasCount(true);
            }
            // ????????????
            esTO.setHotScore(0L);
            // ??????????????????
            BrandEntity brand = brandService.getById(sku.getBrandId());
            esTO.setBrandName(brand.getName());
            esTO.setBrandImg(brand.getLogo());
            // ??????????????????
            CategoryEntity category = categoryService.getById(sku.getCatalogId());
            esTO.setCatalogName(category.getName());
            // ??????????????????
            esTO.setAttrs(attrs);
            return esTO;
        }).collect(Collectors.toList());

        // ????????????????????????elasticsearch
        try {
            R r = searchFeignService.saveProductUp(skuEsTOList);
            if (r.getCode() != 0) {
                log.error("??????es????????????");
            } else {
                // ????????????????????????
                SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
                spuInfoEntity.setId(spuId);
                spuInfoEntity.setPublishStatus(1);
                this.updateById(spuInfoEntity);
            }
        } catch (Exception e) {
            log.error("SKU????????????????????????????????????????????????{}", e);
            e.printStackTrace();
        }

    }

    @Override
    @Transactional
    // TODO ??????????????????
    public void saveSpuInfo(SpuSaveVO vo) {
        // 1?????????spu???????????????pms-spu-info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.baseMapper.insert(spuInfoEntity);

        // 2?????????spu??????????????????pms-spu-info-desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescDao.insert(spuInfoDescEntity);

        // 3?????????spu???????????????pms-spu-images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        // 4?????????spu??????????????????pms-product-attr-value
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

        // 6?????????spu??????????????????gulimall-sms???sms-spu-bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTO spuBoundTO = new SpuBoundTO();
        BeanUtils.copyProperties(bounds, spuBoundTO);
        spuBoundTO.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTO);
        if (r.getCode() != 0) {
            log.error("?????????????????????????????????????????????");
        }

        // 5???????????????spu???????????????sku??????
        List<Skus> skus = vo.getSkus();
        if (CollectionUtils.isNotEmpty(skus)) {
            skus.forEach(sku -> {

                // ??????????????????
                String defaultImage = "";
                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImage = image.getImgUrl();
                    }
                }

                // 5.1???sku??????????????????pms-sku-info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImage);
                skuInfoService.save(skuInfoEntity);

                // 5.2???sku??????????????????pms-sku-images
                List<SkuImagesEntity> skuImagesEntities = sku.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                })
                        // ????????????????????????????????????
                        .filter(entity -> StringUtils.isNotEmpty(entity.getImgUrl()))
                        .collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntities);

                // 5.3???sku????????????????????????pms-sku-sale-attr-value
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

                // 7???sku??????????????????????????????gulimall-sms???sms-sku-ladder???
                SkuReductionTO reductionTO = new SkuReductionTO();
                BeanUtils.copyProperties(sku, reductionTO);
                reductionTO.setSkuId(skuInfoEntity.getSkuId());
                if (reductionTO.getFullCount() > 0 || reductionTO.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
                    // ???????????????????????????????????????????????????????????????????????????????????????
                    R r1 = couponFeignService.saveSkuReduction(reductionTO);
                    if (r1.getCode() != 0) {
                        log.error("?????????????????????????????????????????????");
                    }
                }
            });
        }

    }
}