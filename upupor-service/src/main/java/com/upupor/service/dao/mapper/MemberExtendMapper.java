package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.MemberExtend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberExtendMapper extends BaseMapper<MemberExtend> {


    List<MemberExtend> listByUserIdList(@Param("userIdList") List<String> userIdList);

    /**
     * 根据紧急代码登录
     *
     * @param emergencyCode
     * @return
     */
    Integer countByEmergencyCode(String emergencyCode);


}
