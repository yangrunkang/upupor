package com.upupor.spi.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 删除申请
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/25 13:37
 */
@Data
public class DelApplyReq {

    @NotEmpty(message = "申请id不能为空")
    private String applyId;

    @NotEmpty(message = "用户id不能为空")
    private String userId;

}
