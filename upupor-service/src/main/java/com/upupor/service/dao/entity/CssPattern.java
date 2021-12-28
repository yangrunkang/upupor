package com.upupor.service.dao.entity;

import lombok.Data;

/**
 * Css模式
 */
@Data
public class CssPattern extends BaseEntity {

    private String userId;

    private String cssPatternId;
    private String cssPatternName;

    private String patternContent;
}