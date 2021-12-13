package com.upupor.spi.req;

import lombok.Data;

/**
 * 更新申请请求
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/25 14:55
 */
@Data
public class UpdateApplyReq {
    private String applyId;
    private String userId;
    private Integer status;
}
