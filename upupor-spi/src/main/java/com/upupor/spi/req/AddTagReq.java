package com.upupor.spi.req;

import lombok.Data;

/**
 * 添加标签请求
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/01 15:01
 */
@Data
public class AddTagReq {

    private String pageName;

    private String tagName;

    private String childTagName;

}
