package com.upupor.spi.req;

import lombok.Data;

/**
 * 更新收藏夹请求
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 23:59
 */
@Data
public class UpdateCollectReq {
    private String collectId;
    private String userId;
    private Integer status;
}
