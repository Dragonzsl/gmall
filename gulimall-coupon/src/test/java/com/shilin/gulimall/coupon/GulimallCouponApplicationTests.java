package com.shilin.gulimall.coupon;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.junit.jupiter.api.Test;

import java.util.Date;

//@SpringBootTest
class GulimallCouponApplicationTests {

	@Test
	void contextLoads() {
		Date date = new Date();
		DateTime begin = DateUtil.beginOfDay(date);
		DateTime end = DateUtil.offsetDay(begin, 3);
		System.out.println(date);
		System.out.println("begin：" + begin);
		System.out.println("end：" + end);
	}

}
