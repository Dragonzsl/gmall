package com.shilin.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shilin.common.utils.PageUtils;
import com.shilin.gulimall.member.entity.MemberCollectSpuEntity;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-10-08 19:43:40
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

