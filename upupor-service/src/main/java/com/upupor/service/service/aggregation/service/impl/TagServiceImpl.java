package com.upupor.service.service.aggregation.service.impl;

import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Tag;
import com.upupor.service.dao.mapper.TagMapper;
import com.upupor.service.dto.page.common.TagDto;
import com.upupor.service.service.aggregation.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tag服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/08 09:25
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    @Override
    public List<Tag> getTags() {
        return tagMapper.getAll();
    }

    @Override
    public List<Tag> listByTagIdList(List<String> tagIdList) {
        if (CollectionUtils.isEmpty(tagIdList)) {
            return new ArrayList<>();
        }
        return tagMapper.listByTagIdList(tagIdList);
    }

    @Override
    public List<Tag> getTagsByType(Integer tagType) {
        if (Objects.isNull(tagType)) {
            return new ArrayList<>();
        }
        if (tagType.equals(CcEnum.ContentType.SHORT_CONTENT.getType())) {
            return tagMapper.getAll();
        }

        List<Tag> tags = tagMapper.listByTagType(tagType);
        if (CollectionUtils.isEmpty(tags)) {
            tags = new ArrayList<>();
        }

        return tags;
    }


    @Override
    public List<String> getTagListByName(String tagName) {
        if (StringUtils.isEmpty(tagName)) {
            return new ArrayList<>();
        }

        Set<String> tagIdSet = new HashSet<>();

        List<Tag> tagList = tagMapper.listByName(tagName);
        if (!CollectionUtils.isEmpty(tagList)) {
            tagList.forEach(tag -> {
                tagIdSet.add(tag.getTagId());
            });
        }

        if (CollectionUtils.isEmpty(tagIdSet)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(tagIdSet);
    }

    @Override
    public String getNameById(String tagId) {
        if (StringUtils.isEmpty(tagId)) {
            return null;
        }
        List<Tag> tagList = tagMapper.listByTagIdList(Collections.singletonList(tagId));
        if (!CollectionUtils.isEmpty(tagList)) {
            return tagList.get(0).getTagName();
        }

        return null;
    }

    @Override
    public List<TagDto> listTagNameByTagId(String tagIds) {
        if (StringUtils.isEmpty(tagIds)) {
            return new ArrayList<>();
        }

        // 使用Set集合去重
        Set<TagDto> tagDtoSet = new HashSet<>();

        List<Tag> tags = this.listByTagIdList(Collections.singletonList(tagIds));
        if (!CollectionUtils.isEmpty(tags)) {
            List<TagDto> tagDtoList = tags.stream()
                    .map(tag -> TagDto.builder().tagId(tag.getTagId()).tagName(tag.getTagName()).build())
                    .distinct().collect(Collectors.toList());
            tagDtoSet.addAll(tagDtoList);
        }

        return new ArrayList<>(tagDtoSet);
    }
}
