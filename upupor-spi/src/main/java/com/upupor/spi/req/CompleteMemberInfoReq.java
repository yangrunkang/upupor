package com.upupor.spi.req;

import lombok.Data;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/24 02:39
 */
@Data
public class CompleteMemberInfoReq {

    private String userId;

    private String via;

    private String phone;

    private String introduce;

    private String emergencyCode;

    private Integer age;

    private String birthday;

}
