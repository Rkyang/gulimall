package com.rkyang.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rkyang.common.to.MemberPrice;
import com.rkyang.common.to.SkuReductionTO;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.Query;
import com.rkyang.gulimall.coupon.dao.SkuFullReductionDao;
import com.rkyang.gulimall.coupon.entity.MemberPriceEntity;
import com.rkyang.gulimall.coupon.entity.SkuFullReductionEntity;
import com.rkyang.gulimall.coupon.entity.SkuLadderEntity;
import com.rkyang.gulimall.coupon.service.MemberPriceService;
import com.rkyang.gulimall.coupon.service.SkuFullReductionService;
import com.rkyang.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService ladderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveSkuReduction(SkuReductionTO reductionTO) {
        // sms_sku_ladder
        if (reductionTO.getFullCount() > 0) {
            SkuLadderEntity ladderEntity = new SkuLadderEntity();
            ladderEntity.setSkuId(reductionTO.getSkuId());
            ladderEntity.setFullCount(reductionTO.getFullCount());
            ladderEntity.setDiscount(reductionTO.getDiscount());
            ladderEntity.setAddOther(reductionTO.getCountStatus());
            ladderService.save(ladderEntity);
        }

        // sms_sku_full_reduction
        if (reductionTO.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
            SkuFullReductionEntity fullReductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(reductionTO, fullReductionEntity);
            fullReductionEntity.setAddOther(reductionTO.getCountStatus());
            this.save(fullReductionEntity);
        }

        // sms_member_price
        List<MemberPrice> memberPrice = reductionTO.getMemberPrice();
        List<MemberPriceEntity> memberPriceCollect = memberPrice.stream().map(item -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(reductionTO.getSkuId());
            memberPriceEntity.setMemberLevelId(item.getId());
            memberPriceEntity.setMemberLevelName(item.getName());
            memberPriceEntity.setMemberPrice(item.getPrice());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).filter(item -> item.getMemberPrice().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
        memberPriceService.saveBatch(memberPriceCollect);
    }
}