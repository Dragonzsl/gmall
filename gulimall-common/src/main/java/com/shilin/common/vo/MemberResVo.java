package com.shilin.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-22 12:49:12
 */
@Data
@ToString
public class MemberResVo implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 会员等级id
     */
    private Long levelId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String header;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 生日
     */
    private Date birth;
    /**
     * 所在城市
     */
    private String city;
    /**
     * 职业
     */
    private String job;
    /**
     * 个性签名
     */
    private String sign;
    /**
     * 用户来源
     */
    private Integer sourceType;
    /**
     * 积分
     */
    private Integer integration;
    /**
     * 成长值
     */
    private Integer growth;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 注册时间
     */
    private Date createTime;
    /**
     * 授权用户的UID
     */
    private String uid;
    /**
     * 用户授权的唯一票据
     */
    private String accessToken;
    /**
     * access_token的生命周期，单位是秒数
     */
    private Long expiresIn;
}
