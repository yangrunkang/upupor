package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * Upupor 文件
 *
 * @author runkangyang
 */
@Data
public class File {
    private Long id;

    private String fileMd5;

    private String fileUrl;

    private Long createTime;

    private Integer uploadStatus;

    private Date sysUpdateTime;

    private String userId;

}