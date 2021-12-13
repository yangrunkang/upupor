package com.upupor.service.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.dao.entity.Attention;
import com.upupor.service.dto.page.common.ListAttentionDto;
import com.upupor.spi.req.AddAttentionReq;
import com.upupor.spi.req.DelAttentionReq;

/**
 * 关注服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/01 21:18
 */
public interface AttentionService {

    /**
     * 添加关注
     *
     * @param attention
     * @return
     */
    Integer add(Attention attention);

    Boolean checkExists(String attentionUserId, String userId);

    Attention getAttentionByAttentionId(String attentionId);

    Integer update(Attention attention);

    /**
     * 获取关注者
     *
     * @param userId
     * @return
     */
    ListAttentionDto getAttentions(String userId, Integer pageNum, Integer pageSize);

    /**
     * 获取关注者
     *
     * @param userId
     * @return
     */
    Integer getAttentionNum(String userId);

    Attention select(LambdaQueryWrapper<Attention> queryAttention);

    Boolean attention(AddAttentionReq addAttentionReq);

    Boolean delAttention(DelAttentionReq delAttentionReq);
}
