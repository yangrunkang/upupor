package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * 申请文件
 *
 * @author runkangyang
 */
@Data
public class ApplyDocument {
    private Long id;

    private String applyDocumentId;

    private String applyId;

    private String imgUrl;

    private String upload;

    private String copyWriting;

    private Long createTime;

    private Date sysUpdateTime;
}