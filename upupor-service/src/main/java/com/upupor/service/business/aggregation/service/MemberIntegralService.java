package com.upupor.service.business.aggregation.service;

import com.upupor.service.common.IntegralEnum;
import com.upupor.service.dao.entity.MemberIntegral;
import com.upupor.service.dto.page.common.ListIntegralDto;
import com.upupor.spi.req.GetMemberIntegralReq;

import java.util.List;

/**
 * 用户积分服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/07 23:47
 */
public interface MemberIntegralService {

    /**
     * 检查是否存在
     *
     * @return
     */
    Boolean checkExists(GetMemberIntegralReq memberIntegralReq);

    Boolean addMemberIntegral(MemberIntegral memberIntegral);

    /**
     * 获取用户积分值
     *
     * @return
     */
    Integer getUserIntegral(String userId);

    ListIntegralDto list(String userId, Integer pageNum, Integer pageSize);

    /**
     * @param userId
     * @param ruleId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ListIntegralDto list(String userId, Integer ruleId, Integer pageNum, Integer pageSize);

    boolean addIntegral(IntegralEnum integralEnum, String desc, String userId, String targetId);

    boolean addIntegral(IntegralEnum integralEnum, String userId, String targetId);

    boolean reduceIntegral(IntegralEnum integralEnum, String desc, String userId, String targetId);

    List<MemberIntegral> getByGetMemberIntegralReq(GetMemberIntegralReq getMemberIntegralReq);

    void update(MemberIntegral memberIntegral);
}
