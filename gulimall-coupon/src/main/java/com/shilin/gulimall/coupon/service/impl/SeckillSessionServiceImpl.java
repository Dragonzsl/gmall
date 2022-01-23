package com.shilin.gulimall.coupon.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shilin.common.utils.PageUtils;
import com.shilin.common.utils.Query;
import com.shilin.gulimall.coupon.dao.SeckillSessionDao;
import com.shilin.gulimall.coupon.entity.SeckillSessionEntity;
import com.shilin.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.shilin.gulimall.coupon.service.SeckillSessionService;
import com.shilin.gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getSeckillSession() {
        Date date = new Date();
        DateTime begin = DateUtil.beginOfDay(date);
        DateTime end = DateUtil.offsetDay(begin, 3);

        List<SeckillSessionEntity> seckillSessionEntities = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", begin, end));
        List<SeckillSessionEntity> seckillSessionEntities1 = null;
        if (seckillSessionEntities != null && seckillSessionEntities.size() > 0){
            seckillSessionEntities1 = seckillSessionEntities.stream().peek(seckillSessionEntity -> {
                Long id = seckillSessionEntity.getId();
                List<SeckillSkuRelationEntity> seckillSkuRelationEntities = seckillSkuRelationService
                        .list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", id));
                seckillSessionEntity.setSeckillSkuRelationEntities(seckillSkuRelationEntities);
            }).collect(Collectors.toList());
        }

        return seckillSessionEntities1;
    }

}