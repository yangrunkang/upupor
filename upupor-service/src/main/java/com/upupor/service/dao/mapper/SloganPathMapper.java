package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.SloganPath;
import org.apache.ibatis.annotations.Param;

public interface SloganPathMapper extends BaseMapper<SloganPath> {
    int countByPath(@Param("path") String path);
}
