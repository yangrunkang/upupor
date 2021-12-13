package com.upupor.spi.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 获取文章状态
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/30 01:18
 */
@Data
public class GetContentStatusReq {

    @NotEmpty(message = "内容id不能为空")
    private String contentId;

}
