package com.upupor.spi.req;

import lombok.Data;

/**
 * 登录请求
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/21 02:56
 */
@Data
public class GetMemberReq {
    /**
     * 邮件
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 号码
     */
    private String phone;

    /**
     * 紧急码_只能使用一次_成功使用一次后,必须强制更换,如果不填,默认生成一串密文
     */
    private String emergencyCode;


}
