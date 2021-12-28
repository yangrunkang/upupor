package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * 声明
 *
 * @author runkangyang
 */
@Data
public class Statement extends BaseEntity {

    private Integer statementId;

    private String title;

    private String statement;

    private Long createTime;

    private Date sysUpdateTime;
}