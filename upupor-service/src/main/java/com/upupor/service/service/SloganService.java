package com.upupor.service.service;

import com.upupor.service.dao.entity.Slogan;

import java.util.List;

/**
 * 标语文案服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/23 22:46
 */
public interface SloganService {

    /**
     * 根据类型获取文案
     *
     * @param type
     * @return
     */
    List<Slogan> listByType(Integer type);

    /**
     * 根据具体的path获取slogan
     *
     * @param path
     * @return
     */
    List<Slogan> listByPath(String path);

    /**
     * 添加 文案标语
     *
     * @param slogan
     * @return
     */
    Boolean addSlogan(Slogan slogan);


    Integer countSloganPathByPath(String servletPath);

}