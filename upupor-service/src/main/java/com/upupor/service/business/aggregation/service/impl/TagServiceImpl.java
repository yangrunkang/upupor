/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.service.business.aggregation.service.impl;

import com.upupor.service.business.aggregation.service.TagService;
import com.upupor.service.dao.entity.Tag;
import com.upupor.service.dao.mapper.TagMapper;
import com.upupor.service.dto.page.common.TagDto;
import com.upupor.service.types.ContentType;
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
    public List<Tag> getTagsByType(ContentType tagType) {
        if (Objects.isNull(tagType)) {
            return new ArrayList<>();
        }
        if (tagType.equals(ContentType.SHORT_CONTENT)) {
            return tagMapper.getAll();
        }

        List<Tag> tags = tagMapper.listByTagType(tagType.getType());
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
