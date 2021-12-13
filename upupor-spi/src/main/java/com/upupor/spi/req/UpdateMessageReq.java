package com.upupor.spi.req;

import lombok.Data;

import javax.validation.constraints.Max;

/**
 * 更新消息请求
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/05 17:49
 */
@Data
public class UpdateMessageReq {

    private String userId;

    @Max(value = 2, message = "status超过最大值")
    private Integer status;

    private String messageId;

}
