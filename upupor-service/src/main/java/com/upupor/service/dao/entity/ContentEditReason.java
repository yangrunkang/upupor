package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ContentEditReason {
    private Long id;

    private String contentId;

    private Long createTime;

    private Date sysUpdateTime;

    private String reason;
}