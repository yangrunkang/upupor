package com.upupor.service.service.aggregation.service;

import com.upupor.service.dao.entity.Seo;

import java.util.List;

/**
 * Seo服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/12 04:50
 */
public interface SeoService {

    /**
     * 根据seo id获取 Seo对象
     *
     * @param seoId
     * @return
     */
    Seo getBySeoId(String seoId);

    /**
     * 添加Seo
     *
     * @param seo
     * @return
     */
    Boolean addSeo(Seo seo);

    /**
     * 更新Seo
     *
     * @param seo
     * @return
     */
    Boolean updateSeo(Seo seo);

    List<Seo> listAll();
}
