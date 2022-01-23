package com.shilin.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shilin.common.to.SkuReductionTo;
import com.shilin.common.utils.PageUtils;
import com.shilin.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-10-08 19:33:24
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

