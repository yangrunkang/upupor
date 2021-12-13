package com.upupor.spi.req;

import lombok.Data;

/**
 * 更新待办任务
 *
 * @author cruise
 * @createTime 2021-07-20 14:57
 */
@Data
public class UpdateTodoReq {
    /**
     * 待办Id
     */
    private String todoId;

    /**
     * 待办名称
     */
    private String todoName;

    /**
     * 待办明细
     */
    private String todoDetail;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

}
