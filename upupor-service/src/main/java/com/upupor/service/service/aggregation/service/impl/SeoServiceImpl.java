package com.upupor.service.service.aggregation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.dao.entity.Seo;
import com.upupor.service.dao.mapper.SeoMapper;
import com.upupor.service.service.aggregation.service.SeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Seo服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/12 04:51
 */
@Service
@RequiredArgsConstructor
public class SeoServiceImpl implements SeoService {

    private final SeoMapper seoMapper;

    @Override
    public Seo getBySeoId(String seoId) {
        LambdaQueryWrapper<Seo> query = new LambdaQueryWrapper<Seo>()
                .eq(Seo::getSeoId, seoId);
        return seoMapper.selectOne(query);
    }

    @Override
    public Boolean addSeo(Seo seo) {
        return seoMapper.insert(seo) > 0;
    }

    @Override
    public Boolean updateSeo(Seo seo) {
        return seoMapper.updateById(seo) > 0;
    }

    @Override
    public List<Seo> listAll() {
        return seoMapper.selectList(new LambdaQueryWrapper<>());
    }
}
