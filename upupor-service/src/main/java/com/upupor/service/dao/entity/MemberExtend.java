package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户拓展信息
 */
@Data
public class MemberExtend extends BaseEntity {

    private String userId;

    private String birthday;

    private Integer age;

    private String emergencyCode;

    private Date sysUpdateTime;

    private String introduce;

    /**
     * 实际意义是背景css,中间修改过~
     */
    private String bgImg;

}