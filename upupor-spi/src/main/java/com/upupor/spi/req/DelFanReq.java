package com.upupor.spi.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 删除粉丝
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/02 16:16
 */
@Data
public class DelFanReq {

    @NotEmpty(message = "fanId 不能为空")
    private String fanId;

}
