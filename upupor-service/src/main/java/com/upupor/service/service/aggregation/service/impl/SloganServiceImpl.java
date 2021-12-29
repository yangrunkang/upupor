package com.upupor.service.service.aggregation.service.impl;

import com.upupor.service.dao.entity.Slogan;
import com.upupor.service.dao.mapper.SloganMapper;
import com.upupor.service.dao.mapper.SloganPathMapper;
import com.upupor.service.service.aggregation.service.SloganService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 标语实现层
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/23 22:53
 */
@Service
@RequiredArgsConstructor
public class SloganServiceImpl implements SloganService {

    private final SloganMapper sloganMapper;

    private final SloganPathMapper sloganPathMapper;

    @Override
    public List<Slogan> listByType(Integer type) {
        if (Objects.isNull(type)) {
            return new ArrayList<>();
        }
        return sloganMapper.listByType(type);
    }

    @Override
    public List<Slogan> listByPath(String path) {
        if (StringUtils.isEmpty(path)) {
            return new ArrayList<>();
        }
        return sloganMapper.listByPath(path);
    }

    @Override
    public Boolean addSlogan(Slogan slogan) {
        if (Objects.isNull(slogan)) {
            return false;
        }
        return sloganMapper.insert(slogan) > 0;
    }

    @Override
    public Integer countSloganPathByPath(String servletPath) {
        return sloganPathMapper.countByPath(servletPath);
    }
}
