package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Radio;

import java.util.List;

public interface RadioMapper extends BaseMapper<Radio> {

    Integer countRadioByUserId(String userId);

    List<Radio> list();

    Integer total();
}
