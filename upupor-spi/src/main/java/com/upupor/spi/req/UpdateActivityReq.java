package com.upupor.spi.req;

import lombok.Data;

/**
 * 更新活动
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 03:28
 */
@Data
public class UpdateActivityReq {

    private String activityId;

    private Integer activityStatus;

    private String userId;

}
