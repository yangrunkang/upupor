package com.upupor.service.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * 文章拓展
 *
 * @author runkangyang
 */
@Data
public class ContentExtend extends BaseEntity {

    private String contentId;

    private Date sysUpdateTime;

    private String detailContent;

}