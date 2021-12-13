package com.upupor.spi.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 添加验证码
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 22:11
 */
@Data
public class AddVerifyCodeReq {

    @NotEmpty(message = "验证码来源不能为空")
    private String source;

    @NotEmpty(message = "邮箱不能为空")
    private String email;

}
