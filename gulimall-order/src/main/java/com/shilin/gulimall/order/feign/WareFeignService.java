package com.shilin.gulimall.order.feign;

import com.shilin.common.utils.R;
import com.shilin.gulimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-30 14:16:13
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);

    @GetMapping("/ware/waresku/fare/{addrId}")
    R getFare(@PathVariable("addrId") Long addrId);

    @PostMapping("/ware/waresku/lockStock")
    R orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo);
}
