package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Resource {
    private Long id;

    private String resourceId;

    private String resourceContent;

    private Integer type;

    private Long createTime;

    private Date sysUpdateTime;

    private String comeFrom;

}