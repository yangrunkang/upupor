package com.upupor.service.listener.event;

import com.upupor.service.dao.entity.Attention;
import com.upupor.spi.req.AddAttentionReq;
import lombok.Data;

/**
 * 关注作者事件
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 19:35
 */
@Data
public class AttentionUserEvent {

    private AddAttentionReq addAttentionReq;

    private Attention attention;

}
