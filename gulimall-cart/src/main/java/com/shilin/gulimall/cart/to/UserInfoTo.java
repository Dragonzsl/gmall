package com.shilin.gulimall.cart.to;

import lombok.Data;
import lombok.ToString;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-25 17:57:18
 */
@Data
@ToString
public class UserInfoTo {
    private Long userId;
    private String userKey;
    private boolean tempUser = false;
}
