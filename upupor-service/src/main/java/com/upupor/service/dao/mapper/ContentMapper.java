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

package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dto.dao.ContentIdAndTitle;
import com.upupor.service.dto.dao.MoreCollectDto;
import com.upupor.service.dto.dao.MoreCommentDto;
import com.upupor.service.dto.page.common.CountTagDto;
import com.upupor.service.spi.req.ListContentReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContentMapper extends BaseMapper<Content> {


    ContentIdAndTitle selectById(@Param("id") Long id);

    List<Content> listContent(@Param("listContentReq") ListContentReq listContentReq);


    /**
     * 根据文章类型来获取文章列表
     *
     * @param contentType
     * @return
     */
    List<Content> listContentByContentType(@Param("contentType") Integer contentType, @Param("tag") String tag);


    /**
     * 根据文章id获取内容
     *
     * @param contentIdList
     * @return
     */
    List<Content> listByContentIdList(@Param("contentIdList") List<String> contentIdList);

    /**
     * 最新文章
     *
     * @return
     */
    List<Content> latestContentList();

    /**
     * 最多收藏
     *
     * @return
     */
    List<MoreCollectDto> moreCollectList();

    /**
     * 最多评论
     *
     * @return
     */
    List<MoreCommentDto> moreCommentList();

    /**
     * 文章总数
     */
    Integer total();

    /**
     * 个人profile页面
     *
     * @param userId
     * @return
     */
    List<Content> listAllByUserId(@Param("userId") String userId);

    /**
     * 检查文章状态
     *
     * @param contentId
     * @return
     */
    Integer checkStatusIsOk(@Param("contentId") String contentId);

    /**
     * 根据内容类型获取文章总数
     *
     * @param contentType
     * @return
     */
    Integer getTotalByContentType(@Param("contentType") Integer contentType);

    List<Content> list();


    List<CountTagDto> listAll();

    /**
     * 用户最新的文章
     *
     * @param userId
     * @return
     */
    Content latestContent(String userId);

    /**
     * 目标文章的上一篇及下一篇文章
     *
     * @param contentId
     * @return
     */
    List<Long> lastAndNextContent(@Param("contentId") String contentId);

    /**
     * 统计草稿数目
     *
     * @param userId
     * @return
     */
    Integer countDraft(@Param("userId") String userId);

    /**
     * 随机退件
     *
     * @return
     */
    List<String> randomContent(@Param("notUserId") String notUserId);

    /**
     * 计算用户文章总数
     *
     * @param userId
     * @return
     */
    Integer totalByUserId(@Param("userId") String userId);

}
