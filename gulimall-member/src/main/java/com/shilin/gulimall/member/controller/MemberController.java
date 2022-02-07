package com.shilin.gulimall.member.controller;

import com.shilin.common.exception.CodeEnum;
import com.shilin.common.utils.PageUtils;
import com.shilin.common.utils.R;
import com.shilin.gulimall.member.entity.MemberEntity;
import com.shilin.gulimall.member.exception.MobileExistException;
import com.shilin.gulimall.member.exception.UsernameExistException;
import com.shilin.gulimall.member.service.MemberService;
import com.shilin.gulimall.member.vo.SocialUser;
import com.shilin.gulimall.member.vo.UserLoginVo;
import com.shilin.gulimall.member.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 会员
 *
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-10-08 19:43:40
 */
@RestController
@RequestMapping("/member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

   /* @Autowired
    private CouponFeign couponFeign;

    @RequestMapping("/info1/{id}")
    public R info1(@PathVariable("id") Long id){
        return couponFeign.info(id).put("msg", "查询成功");
    }*/


    /**
     * 用户注册
     *
     * @param userRegisterVo userRegisterVo
     * @return R
     */
    @PostMapping("/register")
    public R register(@RequestBody UserRegisterVo userRegisterVo){

        try {
            memberService.register(userRegisterVo);
        } catch (MobileExistException mobileExistException) {
            return R.error(CodeEnum.MOBILE_EXIST_EXCEPTION.getCode(), CodeEnum.MOBILE_EXIST_EXCEPTION.getMsg());
        } catch (UsernameExistException usernameExistException){
            return R.error(CodeEnum.USERNAME_EXIST_EXCEPTION.getCode(), CodeEnum.USERNAME_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    /**
     * 用户登录
     *
     * @param userLoginVo userLoginVo
     * @return R
     */
    @PostMapping("/login")
    public R login(@RequestBody UserLoginVo userLoginVo){
        MemberEntity memberEntity = memberService.login(userLoginVo);
        if (memberEntity != null){
            return R.ok().setData(memberEntity);
        }else {
            return R.error(CodeEnum.USERNAME_OR_PASSWORD_INVALID_EXCEPTION.getCode(), CodeEnum.USERNAME_OR_PASSWORD_INVALID_EXCEPTION.getMsg());
        }
    }

    /**
     * 用户社交登录
     *
     * @param socialUser userLoginVo
     * @return R
     */
    @PostMapping("/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser socialUser) throws Exception {
        MemberEntity memberEntity = memberService.login(socialUser);
        if (memberEntity != null){
            return R.ok().setData(memberEntity);
        }else {
            return R.error(CodeEnum.USERNAME_OR_PASSWORD_INVALID_EXCEPTION.getCode(), CodeEnum.USERNAME_OR_PASSWORD_INVALID_EXCEPTION.getMsg());
        }
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
