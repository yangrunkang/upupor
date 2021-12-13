package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Attention;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AttentionMapper extends BaseMapper<Attention> {

    /**
     * 检查是否存在重复关注
     *
     * @param attentionUserId
     * @param userId
     * @return
     */
    int checkExists(@Param("attentionUserId") String attentionUserId, @Param("userId") String userId);



    int getAttentionNum(@Param("userId") String userId);


    /**
     * 获取关注者
     *
     * @param userId
     * @return
     */
    List<Attention> getAttentions(@Param("userId") String userId);
}
