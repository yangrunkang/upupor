package com.upupor.service.business.aggregation.service;

import com.upupor.service.dao.entity.Tag;
import com.upupor.service.dto.page.common.TagDto;

import java.util.List;

/**
 * Tag服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/08 09:22
 */
public interface TagService {

    /**
     * 获取所有标签
     * <p>
     * 不用获取子标签
     *
     * @return
     */
    List<Tag> getTags();

    /**
     * 根据标签id获取标签
     *
     * @param tagIdList
     * @return
     */
    List<Tag> listByTagIdList(List<String> tagIdList);

    /**
     * 根据标签类型获取标签
     *
     * @param tagType
     * @return
     */
    List<Tag> getTagsByType(Integer tagType);


    /**
     * 根据名字获取标签id
     *
     * @param tagName
     */
    List<String> getTagListByName(String tagName);

    /**
     * 根据Id获取名称
     *
     * @param tagId
     * @return
     */
    String getNameById(String tagId);

    /**
     * @param tagId
     * @return
     */
    List<TagDto> listTagNameByTagId(String tagId);

}
