package com.shilin.gulimall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-17 18:48:27
 */
@Data
public class UserRegisterVo {

    @NotEmpty(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,16}$", message = "用户名4-16位（字母、数组、下划线、减号）")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    @Pattern(regexp = "^\\S*(?=\\S{6,})(?=\\S*\\d)(?=\\S*[A-Z])(?=\\S*[a-z])(?=\\S*[!@#$%^&*? ])\\S*$",
            message = "密码至少6位，包含至少一个大写字母、一个小写字母、一个数字、一个特殊字符")
    private String password;

    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^(?:(?:\\+|00)86)?1(?:(?:3[\\d])|(?:4[5-7|9])|(?:5[0-3|5-9])|(?:6[5-7])|(?:7[0-8])|(?:8[\\d])|(?:9[1|8|9]))\\d{8}$",
            message = "手机号码格式错误")
    private String phoneNum;

    @NotEmpty(message = "验证码不能为空")
    @Length(min = 4,max = 4,message = "验证码格式错误")
    private String code;
}
