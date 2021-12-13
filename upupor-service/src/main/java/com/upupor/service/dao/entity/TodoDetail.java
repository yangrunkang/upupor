package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * 待办明细
 */
@Data
public class TodoDetail {
    private Long id;

    private String todoId;

    private Long createTime;

    private Date sysUpdateTime;

    private String detail;


}
