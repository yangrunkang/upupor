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

package com.upupor.service.aggregation;

import com.upupor.service.base.ContentService;
import com.upupor.service.base.TagService;
import com.upupor.data.dto.page.TagIndexDto;
import com.upupor.data.dto.page.common.ListContentDto;
import com.upupor.service.outer.req.ListContentReq;
import com.upupor.data.types.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: cruise
 * @created: 2020/08/06 09:21
 */
@Service
@RequiredArgsConstructor
public class TagAggregateService {
    private final ContentService contentService;

    private final TagService tagService;

    public TagIndexDto index(String tagName, Integer pageNum, Integer pageSize) {
        TagIndexDto tagIndexDto = new TagIndexDto(tagName);

        // 根据 tagName 获取tagId
        List<String> tagIdList = tagService.getTagListByName(tagName);
        if (CollectionUtils.isEmpty(tagIdList)) {
            return new TagIndexDto(tagName);
        }

        ListContentReq listContentReq = new ListContentReq();
        listContentReq.setTagIdList(tagIdList);
        listContentReq.setPageNum(pageNum);
        listContentReq.setPageSize(pageSize);
        listContentReq.setStatus(ContentStatus.NORMAL);

        ListContentDto listContentDto = contentService.listContent(listContentReq);
        listContentDto.removeTag();
        tagIndexDto.setListContentDto(listContentDto);
        tagIndexDto.setTagName(tagName);
        return tagIndexDto;
    }
}
