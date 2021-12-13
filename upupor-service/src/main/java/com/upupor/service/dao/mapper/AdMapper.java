package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Ad;

import java.util.List;

public interface AdMapper extends BaseMapper<Ad> {
    List<Ad> all();
}
