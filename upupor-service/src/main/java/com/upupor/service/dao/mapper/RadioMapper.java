package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Radio;

import java.util.List;

public interface RadioMapper extends BaseMapper<Radio> {
    Radio selectByRadioId(String radioId);

    Radio getByRadioIdAndUserId(String radioId, String userId);

    List<Radio> listRadioByUserId(String userId, String searchTitle);

    Integer countRadioByUserId(String userId);

    List<Radio> list();

    Integer total();
}
