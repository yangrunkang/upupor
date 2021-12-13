package com.upupor.service.dao.entity;

import lombok.Data;

/**
 * Css模式
 */
@Data
public class CssPattern {
    private Long id;

    private String userId;

    private String cssPatternId;
    private String cssPatternName;

    private String patternContent;
}