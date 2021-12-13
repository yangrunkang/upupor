package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Ad {
    private Long id;

    private String adId;

    private String adPositionId;

    private String adIntro;

    private String adUrl;

    private String adImg;

    private Integer adStatus;

    private Long createTime;

    private Date sysUpdateTime;

}