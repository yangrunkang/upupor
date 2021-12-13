package com.upupor.spi.req;

import lombok.Data;

/**
 * 添加Redis请求
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 21:44
 */
@Data
public class AddRedisReq {

    private String key;

    private String value;

    private Long timeout;

}
