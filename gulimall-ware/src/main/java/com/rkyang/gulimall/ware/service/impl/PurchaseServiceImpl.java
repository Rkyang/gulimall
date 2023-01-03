package com.rkyang.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rkyang.common.constant.WareConstant;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.Query;
import com.rkyang.common.utils.R;
import com.rkyang.gulimall.ware.dao.PurchaseDao;
import com.rkyang.gulimall.ware.entity.PurchaseDetailEntity;
import com.rkyang.gulimall.ware.entity.PurchaseEntity;
import com.rkyang.gulimall.ware.service.PurchaseDetailService;
import com.rkyang.gulimall.ware.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByUnReceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public R merge(Map<String, Object> params) {
        Long purchaseId = params.get("purchaseId") == null ? null : Long.valueOf(params.get("purchaseId").toString());
        List<Integer> items = (List<Integer>) params.get("items");

        for (Integer item : items) {
            PurchaseDetailEntity byId = purchaseDetailService.getById(item);
            if (byId.getStatus() >= WareConstant.PurchaseDetailStatusEnum.PURCHASEING.getCode()) {
                return R.error("选中的采购需求中存在已领取的需求");
            }
        }

        if (purchaseId == null) {
            // 自动创建采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        // 合并采购需求
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(Long.valueOf(item));
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(collect);

        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(finalPurchaseId);
        purchase.setUpdateTime(new Date());
        this.updateById(purchase);

        return R.ok();
    }

    @Override
    @Transactional
    public void received(List<Long> ids) {
        // 1、过滤掉状态是已分配的数据（正常是前端判断）
        List<PurchaseEntity> purchaseEntityList = ids.stream().map(this::getById)
                .filter(entity ->
                    entity.getStatus().equals(WareConstant.PurchaseStatusEnum.CREATED.getCode()) ||
                    entity.getStatus().equals(WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()))
                .peek(entity -> {
                    entity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                    entity.setUpdateTime(new Date());
                }).collect(Collectors.toList());

        // 2、更新采购单状态
        this.updateBatchById(purchaseEntityList);

        // 3、更新采购需求状态
        purchaseEntityList.forEach(item -> {
            List<PurchaseDetailEntity> detailEntities = purchaseDetailService.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", item.getId()));
            List<PurchaseDetailEntity> collect = detailEntities.stream().map(detail -> {
                PurchaseDetailEntity entity = new PurchaseDetailEntity();
                entity.setId(detail.getId());
                entity.setStatus(WareConstant.PurchaseDetailStatusEnum.PURCHASEING.getCode());
                return entity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);
        });
    }
}