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

package com.upupor.service.business.comment.list.abstracts;

import com.upupor.data.dao.entity.enhance.CommentEnhance;
import com.upupor.data.dto.page.comment.CommentDto;
import com.upupor.data.types.ContentType;
import com.upupor.framework.CcConstant;
import com.upupor.service.business.comment.list.ContentCommentList;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Yang Runkang (cruise)
 * @date 2023年01月09日 14:58
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractCommentList<T> {
    protected List<CommentDto> commentDtoList;
    protected List<CommentEnhance> commentEnhanceList;

    /**
     * 是否适配
     *
     * @return
     */
    protected abstract Boolean isFit(ContentType commentSource);


    /**
     * 过滤出目标Id
     *
     * @return
     */
    protected abstract List<String> filterTargetIdList();

    protected abstract void createCommentDto(Map<String, T> tMap, CommentEnhance commentEnhance);

    /**
     * 过滤出的Id转化为具体的泛型Map
     *
     * @param filteredIdList
     * @return
     */
    protected abstract Map<String, T> filteredIdConvertToTMap(List<String> filteredIdList);

    /**
     * 计算带页码的评论锚点
     *
     * @param floorNum
     * @return
     */
    protected String calcPageAnchor(Long floorNum) {
        boolean b = floorNum % CcConstant.Page.SIZE == 0;
        long l = floorNum / CcConstant.Page.SIZE;
        return "?pageNum=" + (b ? 1 : (l + 1)) + "#comment_" + floorNum;
    }

//    public static void main(String[] args) {
//        System.out.println(new ContentCommentList().calcPageAnchor(30L)); //1
//        System.out.println(new ContentCommentList().calcPageAnchor(29L)); //1
//        System.out.println(new ContentCommentList().calcPageAnchor(31L)); //2
//    }

    public List<CommentDto> handle(List<CommentEnhance> commentEnhanceList) {
        init(commentEnhanceList);
        List<String> filterTargetIdList = filterTargetIdList();
        if (CollectionUtils.isEmpty(filterTargetIdList)) {
            return new ArrayList<>();
        }
        Map<String, T> tMap = filteredIdConvertToTMap(filterTargetIdList);
        for (CommentEnhance commentEnhance : commentEnhanceList) {
            if (isFit(commentEnhance.getComment().getCommentSource())) {
                createCommentDto(tMap, commentEnhance);
            }
        }
        return commentDtoList;
    }

    private void init(List<CommentEnhance> commentEnhanceList) {
        commentDtoList = new ArrayList<>();
        this.commentEnhanceList = commentEnhanceList;
    }

}
