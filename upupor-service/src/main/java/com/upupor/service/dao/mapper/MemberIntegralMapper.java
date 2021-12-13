package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.MemberIntegral;
import com.upupor.spi.req.GetMemberIntegralReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberIntegralMapper extends BaseMapper<MemberIntegral> {

    /**
     * 根据条件检索数目
     *
     * @param getMemberIntegralReq
     * @return
     */
    Integer countByCondition(GetMemberIntegralReq getMemberIntegralReq);

    Integer getTotalUserIntegral(@Param("userId") String userId);

    /**
     * 每日签到的用户Id集合
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<String> dailyPointsUserIdList(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

}
