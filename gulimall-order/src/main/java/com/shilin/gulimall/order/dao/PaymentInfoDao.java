package com.shilin.gulimall.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shilin.gulimall.order.entity.PaymentInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-10-08 18:11:34
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
