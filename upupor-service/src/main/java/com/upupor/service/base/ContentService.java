/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.service.base;

import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.ContentData;
import com.upupor.data.dao.entity.ContentEditReason;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dao.entity.enhance.RadioEnhance;
import com.upupor.data.dto.OperateContentDto;
import com.upupor.data.dto.page.common.CountTagDto;
import com.upupor.data.dto.page.common.ListContentDto;
import com.upupor.data.types.ContentType;
import com.upupor.data.types.PinnedStatus;
import com.upupor.data.types.SearchContentType;
import com.upupor.service.outer.req.ListContentReq;
import com.upupor.service.outer.req.content.CreateContentReq;
import com.upupor.service.outer.req.content.UpdateContentReq;
import com.upupor.service.outer.req.content.UpdateStatusReq;

import java.util.List;

/**
 * 内容服务
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 07:58
 */
public interface ContentService {
    /**
     * 获取文章详情
     *
     * @param contentId
     * @return
     */
    ContentEnhance getContentDetail(String contentId);

    /**
     * 获取文章
     *
     * @param contentId
     * @return
     */
    ContentEnhance getNormalContent(String contentId);


    ContentEnhance getContentByContentIdNoStatus(String contentId);

    /**
     * 获取文章列表
     *
     * @param listContentReq
     * @return
     */
    ListContentDto listContent(ListContentReq listContentReq);

    /**
     * 根据文章类型来获取文章列表
     *
     * @param contentType
     * @param pageNum
     * @param pageSize
     * @return
     */
    ListContentDto listContentByContentType(ContentType contentType, Integer pageNum, Integer pageSize, String tag);

    /**
     * 不同类型的文章列表
     *
     * @param searchType
     * @param pageNum
     * @param pageSize
     * @return
     * @note: 全部文章、最近更新的、新内容
     */
    ListContentDto typeContentList(SearchContentType searchType, Integer pageNum, Integer pageSize);

    /**
     * 添加内容
     *
     * @param createContentReq
     * @return
     */
    OperateContentDto addContent(CreateContentReq createContentReq);

    /**
     * 插入内容
     *
     * @param content
     * @return
     */
    Boolean insertContent(ContentEnhance content);


    /**
     * 更新内容
     *
     * @param updateContentReq
     * @return
     */
    OperateContentDto updateContent(UpdateContentReq updateContentReq);

    /**
     * 更新内容
     *
     * @param content
     * @return
     */
    Boolean updateContent(ContentEnhance content);

    /**
     * 根据内容Id集合获取内容
     *
     * @param contentIdList
     * @return
     */
    List<ContentEnhance> listByContentIdList(List<String> contentIdList);

    /**
     * 根据用户id查询所有文章
     *
     * @param userIdList
     * @author runkangyang (cruise)
     * @date 2020.01.26 19:59
     */
    List<ContentEnhance> listAllByUserId(List<String> userIdList);

    /**
     * 文章总数
     */
    Integer total();

    /**
     * 绑定文章数据
     *
     * @param contentList
     */
    void bindContentData(List<ContentEnhance> contentList);

    /**
     * 绑定电台数据
     *
     * @param radioList
     */
    void bindRadioContentData(List<RadioEnhance> radioList);


    /**
     * 绑定文章扩展信息
     *
     * @param content
     */
    void bindContentExtend(ContentEnhance content);

    /**
     * 绑定用户名
     *
     * @param listContentDto
     */
    void bindContentMember(ListContentDto listContentDto);

    /**
     * 绑定用户名
     *
     * @param content
     */
    void bindContentMember(ContentEnhance content);

    /**
     * 绑定用户名
     *
     * @param contentList
     */
    void bindContentMember(List<ContentEnhance> contentList);


    Boolean currentUserIsAttentionAuthor(String contentUserId);

    /**
     * 获取管理页的内容详情
     *
     * @param contentId
     * @return
     */
    ContentEnhance getManageContentDetail(String contentId);

    /**
     * 获取文章
     *
     * @return
     */
    List<Content> list(Integer pageNum, Integer pageSize);

    /**
     * 初始化文章数据
     *
     * @param targetId
     */
    void initContendData(String targetId);

    /**
     * 添加内容浏览数
     *
     * @param targetId
     */
    void viewNumPlusOne(String targetId);

    /**
     * 根据置顶状态获取文章列表
     *
     * @param pinnedStatus
     * @return
     */
    List<ContentEnhance> getContentListByPinned(PinnedStatus pinnedStatus, String userId);


    void handlePinnedContent(ListContentDto listContentDto, String userId);

    ContentEditReason latestEditReason(String contentId);


    void bindContentEditReason(ContentEnhance content);

    void bindContentStatement(ContentEnhance content);

    /**
     * 罗列所有的标签
     *
     * @return
     */
    List<CountTagDto> listAllTag();

    List<CountTagDto> listCountByTagIds(List<String> tagIdList);

    /**
     * 绑定点赞的人
     *
     * @param content
     */
    void bindLikesMember(ContentEnhance content);

    /*
     * 绑定文章标签
     *
     * @param content
     */
    void bindContentTag(List<ContentEnhance> contentList);

    /**
     * 上一篇、下一篇
     *
     * @param content
     * @return
     */
    void lastAndNextContent(ContentEnhance content);


    /**
     * 更新内容状态
     *
     * @param updateStatusReq
     * @return
     */
    OperateContentDto updateContentStatus(UpdateStatusReq updateStatusReq);


    /**
     * 随机文章
     *
     * @return
     */
    List<ContentEnhance> randomContent(String notUserId);

    /**
     * 获取文章总数
     *
     * @param userId
     * @return
     */
    Integer getUserTotalContentNum(String userId);

    void updateContentData(ContentData contentData);

    /**
     * 最近一周新增的文章
     *
     * @return
     * @note: 只显示15条
     */
    List<ContentEnhance> latestContentList();

    /**
     * 是否存在
     *
     * @param contentId
     * @return
     */
    Boolean exists(String contentId);

    void handleExistsDraft(ListContentDto listContentDto);
}
