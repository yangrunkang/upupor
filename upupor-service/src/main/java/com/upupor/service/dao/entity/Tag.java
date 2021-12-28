package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;


/**
 * 标签
 *
 * @author runkangyang (cruise)
 * @date 2020.01.08 02:23
 */
@Data
public class Tag extends BaseEntity {

    /**
     * 标签Id
     */
    private String tagId;

    /**
     * 标签类型 1-技术类 2-问答类
     */
    private Integer tagType;

    /**
     * 状态 0-正常
     */
    private Integer status;

    private String tagName;

    private Long createTime;

    private Date sysUpdateTime;

    private String icon;
}