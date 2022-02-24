package com.shilin.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shilin.common.utils.PageUtils;
import com.shilin.gulimall.member.entity.GrowthChangeHistoryEntity;

import java.util.Map;

/**
 * 成长值变化历史记录
 *
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-10-08 19:43:40
 */
public interface GrowthChangeHistoryService extends IService<GrowthChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

