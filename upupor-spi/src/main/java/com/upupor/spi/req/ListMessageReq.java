package com.upupor.spi.req;

import lombok.Data;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 00:28
 */
@Data
public class ListMessageReq {

    private String userId;

    private Integer status;

    private Integer pageNum;

    private Integer pageSize;

}
