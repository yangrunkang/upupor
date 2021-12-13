package com.upupor.spi.req;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 更新评论
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/21 10:32
 */
@Data
public class UpdateCommentReq {

    @NotEmpty(message = "评论ID不能为空")
    private String commentId;

    @Min(value = 0)
    @Max(value = 2)
    @NotNull(message = "状态不能为空")
    private Integer status;

}
