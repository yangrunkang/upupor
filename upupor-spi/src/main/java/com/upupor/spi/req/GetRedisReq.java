package com.upupor.spi.req;

import lombok.Data;

/**
 * 获取Redis Key
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 21:56
 */
@Data
public class GetRedisReq {
    private String key;
}
