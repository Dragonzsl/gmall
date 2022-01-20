package com.shilin.gulimall.auth.vo;

import lombok.Data;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-21 18:21:09
 */
@Data
public class SocialUser {
    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
}
