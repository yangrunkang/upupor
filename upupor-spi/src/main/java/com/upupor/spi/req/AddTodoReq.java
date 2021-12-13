package com.upupor.spi.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * @author cruise
 * @createTime 2021-07-20 14:54
 */
@Data
public class AddTodoReq {
    /**
     * 待办
     */
    @Length(max = 256, message = "待办内容过长,最多可输入512个字")
    @NotEmpty(message = "待办不能为空")
    private String todo;

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
