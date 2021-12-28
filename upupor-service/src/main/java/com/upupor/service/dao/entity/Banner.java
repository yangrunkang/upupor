package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Banner extends BaseEntity {

    private String bannerId;

    private String bannerTitle;

    private String bannerDesc;

    private Integer bannerStatus;

    private String bannerHref;

    private String bannerPic;

    private Long createTime;

    private Date sysUpdateTime;

}