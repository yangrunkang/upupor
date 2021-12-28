package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * 待办明细
 */
@Data
public class TodoDetail extends BaseEntity {

    private String todoId;

    private Long createTime;

    private Date sysUpdateTime;

    private String detail;


}
