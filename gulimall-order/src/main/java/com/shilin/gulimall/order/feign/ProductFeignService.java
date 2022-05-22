package com.shilin.gulimall.order.feign;

import com.shilin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-02 11:06:16
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    @GetMapping("/product/spuinfo/skuId/{skuId}")
    R spuInfoBySkuId(@PathVariable("skuId") Long skuId);
}
