package com.upupor.spi.req;

import lombok.Data;

/**
 * 添加收藏请求
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 18:26
 */
@Data
public class AddCollectReq {

    /**
     * 收藏的对象值
     */
    private String collectValue;

    /**
     * 收藏类型
     */
    private Integer collectType;

    /**
     * true:已经收藏  false:未收藏
     */
    private Boolean isCollect;

}
