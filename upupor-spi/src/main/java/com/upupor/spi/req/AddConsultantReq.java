package com.upupor.spi.req;

import lombok.Data;

/**
 * 添加申请咨询服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/05 10:20
 */
@Data
public class AddConsultantReq {

    private String topic;

    private String desc;

    private Integer type;

}
