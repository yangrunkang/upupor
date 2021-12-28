package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * Seo
 */
@Data
public class Seo extends BaseEntity {

    private String seoId;

    private Integer seoStatus;

    private Long createTime;

    private Date sysUpdateTime;

    private String seoContent;

}