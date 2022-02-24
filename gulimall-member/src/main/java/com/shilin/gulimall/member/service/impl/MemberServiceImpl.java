package com.shilin.gulimall.member.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shilin.common.utils.HttpUtils;
import com.shilin.common.utils.PageUtils;
import com.shilin.common.utils.Query;
import com.shilin.gulimall.member.dao.MemberDao;
import com.shilin.gulimall.member.entity.MemberEntity;
import com.shilin.gulimall.member.entity.MemberLevelEntity;
import com.shilin.gulimall.member.exception.MobileExistException;
import com.shilin.gulimall.member.exception.UsernameExistException;
import com.shilin.gulimall.member.service.MemberLevelService;
import com.shilin.gulimall.member.service.MemberService;
import com.shilin.gulimall.member.vo.SocialUser;
import com.shilin.gulimall.member.vo.UserLoginVo;
import com.shilin.gulimall.member.vo.UserRegisterVo;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(UserRegisterVo userRegisterVo) {
        MemberEntity memberEntity = new MemberEntity();
        //设置默认会员等级
        MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultMemberLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());

        //设置密码，密码需要加密存储
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodePassword = bCryptPasswordEncoder.encode(userRegisterVo.getPassword());
        memberEntity.setPassword(encodePassword);

        //设置用户名
        this.checkUsername(userRegisterVo.getUserName());
        memberEntity.setUsername(userRegisterVo.getUserName());

        //设置手机号
        this.checkMobile(userRegisterVo.getPhoneNum());
        memberEntity.setMobile(userRegisterVo.getPhoneNum());

        //注册时间
        memberEntity.setCreateTime(DateUtil.dateSecond());

        baseMapper.insert(memberEntity);
    }

    @Override
    public MemberEntity login(UserLoginVo userLoginVo) {
        String loginAccount = userLoginVo.getLoginAccount();
        String password = userLoginVo.getPassword();
        //根据账号查询用户信息
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginAccount)
                .or()
                .eq("mobile", loginAccount)
                .or()
                .eq("email", loginAccount));
        if (memberEntity == null){
            return null;
        }else {
            //查看密码是否一致
            String passwordFromDB = memberEntity.getPassword();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean matches = bCryptPasswordEncoder.matches(password, passwordFromDB);
            if (matches){
                return memberEntity;
            }else {
                return null;
            }
        }
    }

    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {

        //判断当前用户是否已将社交登录过
        MemberEntity member = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("uid", socialUser.getUid()));
        if (member != null){
            //已注册
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            memberEntity.setId(member.getId());
            baseMapper.updateById(memberEntity);

            member.setAccessToken(socialUser.getAccess_token());
            member.setExpiresIn(socialUser.getExpires_in());
            return member;
        }else {
            //未注册
            //https://api.weibo.com/2/users/show.json?access_token=2.00cv5CcG32fcWCbed26f564c0Cf84i&uid=
            MemberEntity memberEntity = new MemberEntity();
            try {
                Map<String ,String > headers = new HashMap<>();
                Map<String ,String > querys = new HashMap<>();
                querys.put("access_token",socialUser.getAccess_token());
                querys.put("uid", socialUser.getUid());
                HttpResponse httpResponse = HttpUtils.doGet("https://api.weibo.com",
                        "/2/users/show.json",
                        "get",
                        headers,
                        querys);
                String json = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                //昵称
                String nickname = (String) jsonObject.get("name");
                memberEntity.setNickname(nickname);
                //头像
                String  header = (String) jsonObject.get("profile_image_url");
                memberEntity.setHeader(header);
                //性别
                String  gender = (String) jsonObject.get("gender");
                memberEntity.setGender(("m".equalsIgnoreCase(gender) ? 1 : 0));
                //城市
                String  location = (String) jsonObject.get("location");
                memberEntity.setCity(location);
                //创建时间
                memberEntity.setCreateTime(DateUtil.dateSecond());
                //会员等级
                MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultMemberLevel();
                memberEntity.setLevelId(memberLevelEntity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //存活时间
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            //uid
            memberEntity.setUid(socialUser.getUid());
            //access_token
            memberEntity.setAccessToken(socialUser.getAccess_token());
            //注册
            baseMapper.insert(memberEntity);
            return memberEntity;

        }
    }

    private void checkMobile(String phoneNum) throws MobileExistException  {
        Integer mobile = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phoneNum));
        if (mobile > 0){
            throw new MobileExistException("手机号码已存在");
        }
    }

    private void checkUsername(String userName) throws UsernameExistException {
        Integer username = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (username > 0){
            throw new UsernameExistException("用户名已存在");
        }
    }

}
