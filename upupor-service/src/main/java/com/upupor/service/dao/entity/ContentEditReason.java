package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ContentEditReason extends BaseEntity {

    private String contentId;

    private Long createTime;

    private Date sysUpdateTime;

    private String reason;
}