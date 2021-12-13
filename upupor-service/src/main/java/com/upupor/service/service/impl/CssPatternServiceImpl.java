package com.upupor.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.dao.entity.CssPattern;
import com.upupor.service.dao.mapper.CssPatternMapper;
import com.upupor.service.dto.page.common.ListCssPatternDto;
import com.upupor.service.service.CssPatternService;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author YangRunkang(cruise)
 * @date 2020/10/02 21:25
 */
@Service
@RequiredArgsConstructor
public class CssPatternServiceImpl implements CssPatternService {

    private final CssPatternMapper cssPatternMapper;

    @Override
    public ListCssPatternDto getAll(String userId) {

        List<CssPattern> cssPatternList = cssPatternMapper.selectList(new LambdaQueryWrapper<>());
        if (CollectionUtils.isEmpty(cssPatternList)) {
            cssPatternList = new ArrayList<>();
        }

        if (CollectionUtils.isEmpty(cssPatternList)) {
            return new ListCssPatternDto();
        }

        ListCssPatternDto listCssPatternDto = new ListCssPatternDto();
        listCssPatternDto.setPatternList(cssPatternList);
        listCssPatternDto.setTotal((long) cssPatternList.size());
        listCssPatternDto.setPaginationHtml(Strings.EMPTY);

        if (!StringUtils.isEmpty(userId)) {
            CssPattern userCssPattern = getByUserId(userId);
            if (Objects.nonNull(userCssPattern)) {
                listCssPatternDto.setUserDefinedCss(userCssPattern);
            }
        }

        return listCssPatternDto;
    }

    @Override
    public CssPattern getByUserId(String userId) {
        LambdaQueryWrapper<CssPattern> query = new LambdaQueryWrapper<CssPattern>()
                .eq(CssPattern::getUserId, userId);
        return cssPatternMapper.selectOne(query);
    }

    @Override
    public Boolean add(CssPattern newCss) {
        return cssPatternMapper.insert(newCss) > 0;
    }

    @Override
    public Boolean updateByUserId(CssPattern updateCss) {
        return cssPatternMapper.updateById(updateCss) > 0;
    }
}
