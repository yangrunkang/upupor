package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Apply;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApplyMapper extends BaseMapper<Apply> {

    Integer total();

    /**
     * 根据用户id获取申请列表
     *
     * @param userId
     * @return
     */
    List<Apply> listApplyListByUserId(@Param("userId") String userId);

    List<Apply> listApplyListByUserIdManage(@Param("userId") String userId);
}
