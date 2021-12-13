package com.upupor.spi.req;

import lombok.Data;

/**
 * @author YangRunkang(cruise)
 * @date 2020/10/02 23:21
 */
@Data
public class UpdateCssReq {
    /**
     * 自定义css
     */
    private String selfDefineCss;

    /**
     * cssPattern内容
     */
    private String cssPatternValue;

    private String userId;
}
