package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BuriedPointData extends BaseEntity {

    private String pointId;

    private String sessionId;

    private String userId;

    private Integer pointType;

    private String requestMethod;

    private String ip;

    private Long port;

    private String servletPath;

    private String parameters;

    private Long createTime;

    private Date sysUpdateTime;

}