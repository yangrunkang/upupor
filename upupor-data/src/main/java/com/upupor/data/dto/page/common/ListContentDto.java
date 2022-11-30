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

package com.upupor.data.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容列表(文章通用结构)
 *
 * @author: YangRunkang(cruise)
 * @created: 2020/01/05 13:21
 */
@Data
public class ListContentDto extends BaseListDto {

    /**
     * 内容集合
     */
    private List<ContentEnhance> contentEnhanceList;

    /**
     * 置顶的文章
     */
    private ContentEnhance pinnedContentEnhance;

    public ListContentDto(PageInfo pageInfo) {
        super(pageInfo);
        this.contentEnhanceList = new ArrayList();
    }

    public ListContentDto() {
        this.contentEnhanceList = new ArrayList();
    }

    public List<ContentEnhance> getContentEnhanceList() {
        if (CollectionUtils.isEmpty(contentEnhanceList)) {
            return new ArrayList<>();
        }
        return contentEnhanceList;
    }

    public void removeTag() {
        if (CollectionUtils.isEmpty(contentEnhanceList)) {
            return;
        }
        contentEnhanceList.forEach(s -> s.setTagDtoList(Lists.newArrayList()));
    }
}
