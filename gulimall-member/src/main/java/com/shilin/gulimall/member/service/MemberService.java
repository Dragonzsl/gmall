package com.shilin.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shilin.common.utils.PageUtils;
import com.shilin.gulimall.member.entity.MemberEntity;
import com.shilin.gulimall.member.vo.SocialUser;
import com.shilin.gulimall.member.vo.UserLoginVo;
import com.shilin.gulimall.member.vo.UserRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-10-08 19:43:40
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(UserRegisterVo userRegisterVo);

    MemberEntity login(UserLoginVo userLoginVo);

    MemberEntity login(SocialUser socialUser) throws Exception;

}

