package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * 声明
 *
 * @author runkangyang
 */
@Data
public class Statement {

    private Long id;

    private Integer statementId;

    private String title;

    private String statement;

    private Long createTime;

    private Date sysUpdateTime;
}