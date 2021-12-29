package com.upupor.service.business.aggregation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.dao.entity.Collect;
import com.upupor.service.dto.page.common.ListCollectDto;

/**
 * 收藏服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 15:09
 */
public interface CollectService {

    /**
     * 根据用户id获取收藏内容
     *
     * @param userId
     * @return
     */
    ListCollectDto listByUserId(String userId, Integer pageNum, Integer pageSize);

    ListCollectDto listByUserIdManage(String userId, Integer pageNum, Integer pageSize);

    /**
     * 检查用户是否已经收藏
     *
     * @param contentId
     * @param userId
     */
    Boolean existsCollectContent(String contentId, String userId);

    Boolean existsCollectContent(String contentId, Integer collectStatus, String userId);

    Collect getCollect(String contentId, Integer collectStatus, String userId);

    /**
     * 添加收藏
     *
     * @param collect
     * @return
     */
    Integer addCollect(Collect collect);


    Integer collectNum(Integer collectType, String collectValue);

    Collect select(LambdaQueryWrapper<Collect> queryCollect);

    Integer update(Collect collect);
}
