package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Feedback {
    private Long id;

    private String userId;

    private String feedbackId;

    private Integer status;

    private String reply;

    private Long createTime;

    private Date sysUpdateTime;

    private String feedbackContent;


}