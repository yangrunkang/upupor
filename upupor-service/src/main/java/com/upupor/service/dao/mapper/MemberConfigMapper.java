package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.MemberConfig;
import org.apache.ibatis.annotations.Param;

public interface MemberConfigMapper extends BaseMapper<MemberConfig> {

    Integer countByUserId(@Param("userId") String userId);

}
