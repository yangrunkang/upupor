package com.upupor.spi.req;

import lombok.Data;

/**
 * 获取用户积分数据请求
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/07 23:49
 */
@Data
public class GetMemberIntegralReq {
    private String userId;
    private Integer ruleId;
    private String targetId;
    private long startCreateTime;
    private long endCreateTime;
    private Integer status;
}
