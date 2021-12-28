package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Feedback extends BaseEntity {

    private String userId;

    private String feedbackId;

    private Integer status;

    private String reply;

    private Long createTime;

    private Date sysUpdateTime;

    private String feedbackContent;


}