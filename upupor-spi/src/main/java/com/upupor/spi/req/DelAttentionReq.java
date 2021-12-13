package com.upupor.spi.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 删除关注者
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/02 01:50
 */
@Data
public class DelAttentionReq {

    @NotEmpty(message = "attentionId 不能为空")
    private String attentionId;

}
