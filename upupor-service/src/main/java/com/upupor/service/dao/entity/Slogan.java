package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * 文案标语
 *
 * @author runkangyang (cruise)
 * @date 2020.02.23 22:45
 */
@Data
public class Slogan extends BaseEntity {

    private String sloganId;

    private String sloganName;

    private String sloganText;

    /**
     * 背景样式
     */
    private String bgStyle;

    private Integer sloganType;

    private Integer sloganStatus;

    private Long createTime;

    private Date sysUpdateTime;
}