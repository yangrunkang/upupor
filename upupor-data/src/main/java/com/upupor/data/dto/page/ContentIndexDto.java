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

package com.upupor.data.dto.page;

import com.upupor.data.dao.entity.enhance.ContentEditReasonEnhance;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容详情Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/12 01:16
 */
@Data
public class ContentIndexDto {

    /**
     * 作者其他的文章
     */
    List<ContentEnhance> authorOtherContentEnhanceList;
    private ContentEnhance contentEnhance;
    /**
     * true:已经收藏  false:未收藏
     */
    private Boolean currUserIsCollect;
    /**
     * true:已经点赞  false:未点赞
     */
    private Boolean currUserIsClickLike;
    /**
     * true:已关注  false:未关注
     */
    private Boolean currUserIsAttention;
    /**
     * 是否有电台
     */
    private Boolean hasRadio;
    private ContentEditReasonEnhance contentEditReasonEnhance;

    /**
     * 推荐文章
     */
    private List<ContentEnhance> randomContentEnhanceList;

    /**
     * 根据当前用户点击的文章类型来创建内容
     */
    private HrefDesc createContentDesc;

    public ContentIndexDto() {
        this.currUserIsCollect = false;
        this.currUserIsAttention = false;
        this.hasRadio = false;
        this.authorOtherContentEnhanceList = new ArrayList<>();
        this.randomContentEnhanceList = new ArrayList<>();
    }
}
