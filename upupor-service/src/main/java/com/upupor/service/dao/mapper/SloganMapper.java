package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Slogan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SloganMapper extends BaseMapper<Slogan> {

    List<Slogan> listByType(@Param("type") Integer type);

    List<Slogan> listByPath(@Param("path") String path);



}
