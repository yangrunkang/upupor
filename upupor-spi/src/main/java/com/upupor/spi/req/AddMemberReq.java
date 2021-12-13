package com.upupor.spi.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 注册请求
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/20 02:58
 */
@Data
public class AddMemberReq {

    private String userName;

    @NotEmpty
    private String password;

    private String email;

    private String phone;

    private String verifyCode;

    // 额外信息(可以在登录页面提示用户展开)
    private String birthday;

    private Integer age;

    private String emergencyCode;

    private String introduce;

    private String via;

}
