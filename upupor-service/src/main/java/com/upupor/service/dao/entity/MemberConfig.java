package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户配置
 *
 * @author runkangyang
 */
@Data
public class MemberConfig extends BaseEntity {

    private String configId;

    private String userId;

    private String bgImg;

    private Long intervalTimeCreateContent;

    private Long createTime;

    private Date sysUpdateTime;

    /**
     * 是否开启邮件通知 0-开启 1-关闭
     */
    private Integer openEmail;

}