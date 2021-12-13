package com.upupor.spi.req;

import lombok.Data;

/**
 * 更新密码
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/25 21:30
 */
@Data
public class UpdatePasswordReq {
    private String email;
    private String password;
    private String verifyCode;
}
