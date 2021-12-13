package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Tag;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {

    List<Tag> getAll();

    /**
     * 根据tagId获取标签
     *
     * @param tagIdList
     * @return
     */
    List<Tag> listByTagIdList(@Param("list") List<String> tagIdList);

    List<Tag> listByTagType(@Param("tagType") Integer tagType);

    List<Tag> listByName(@Param("tagName") String tagName);
}
