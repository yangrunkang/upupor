package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Fans;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FansMapper extends BaseMapper<Fans> {

    int getFansNum(@Param("userId") String userId);

    List<Fans> getFans(@Param("userId") String userId);

    Fans getFanByFanId(String fanId);
}
