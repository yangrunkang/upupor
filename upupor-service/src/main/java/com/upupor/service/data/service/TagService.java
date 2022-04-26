/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
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

package com.upupor.service.data.aggregation.service;

import com.upupor.service.data.dao.entity.Tag;
import com.upupor.service.dto.page.common.TagDto;
import com.upupor.service.types.ContentType;

import java.util.List;

/**
 * Tag服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/08 09:22
 */
public interface TagService {

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
    List<Tag> getTagsByType(ContentType tagType);


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
