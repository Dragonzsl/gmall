package com.shilin.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shilin.common.utils.PageUtils;
import com.shilin.gulimall.member.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-10-08 19:43:40
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

