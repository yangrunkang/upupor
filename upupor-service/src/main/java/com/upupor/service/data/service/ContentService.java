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

package com.upupor.service.data.service;

import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.dao.entity.ContentData;
import com.upupor.service.data.dao.entity.ContentEditReason;
import com.upupor.service.data.dao.entity.Radio;
import com.upupor.service.dto.OperateContentDto;
import com.upupor.service.dto.page.common.CountTagDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.outer.req.ListContentReq;
import com.upupor.service.outer.req.content.CreateContentReq;
import com.upupor.service.outer.req.content.UpdateContentReq;
import com.upupor.service.outer.req.content.UpdateStatusReq;
import com.upupor.service.types.ContentType;
import com.upupor.service.types.PinnedStatus;
import com.upupor.service.types.SearchContentType;

import java.util.List;

/**
 * ????????????
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 07:58
 */
public interface ContentService {
    /**
     * ??????????????????
     *
     * @param contentId
     * @return
     */
    Content getContentDetail(String contentId);

    /**
     * ????????????
     *
     * @param contentId
     * @return
     */
    Content getNormalContent(String contentId);


    Content getContentByContentIdNoStatus(String contentId);

    /**
     * ??????????????????
     *
     * @param listContentReq
     * @return
     */
    ListContentDto listContent(ListContentReq listContentReq);

    /**
     * ???????????????????????????????????????
     *
     * @param contentType
     * @param pageNum
     * @param pageSize
     * @return
     */
    ListContentDto listContentByContentType(ContentType contentType, Integer pageNum, Integer pageSize, String tag);

    /**
     * ???????????????????????????
     *
     * @param searchType
     * @param pageNum
     * @param pageSize
     * @return
     * @note: ??????????????????????????????????????????
     */
    ListContentDto typeContentList(SearchContentType searchType, Integer pageNum, Integer pageSize);

    /**
     * ????????????
     *
     * @param createContentReq
     * @return
     */
    OperateContentDto addContent(CreateContentReq createContentReq);

    /**
     * ????????????
     *
     * @param content
     * @return
     */
    Boolean insertContent(Content content);


    /**
     * ????????????
     *
     * @param updateContentReq
     * @return
     */
    OperateContentDto updateContent(UpdateContentReq updateContentReq);

    /**
     * ????????????
     *
     * @param content
     * @return
     */
    Boolean updateContent(Content content);

    /**
     * ????????????Id??????????????????
     *
     * @param contentIdList
     * @return
     */
    List<Content> listByContentIdList(List<String> contentIdList);

    /**
     * ????????????id??????????????????
     *
     * @param userIdList
     * @author runkangyang (cruise)
     * @date 2020.01.26 19:59
     */
    List<Content> listAllByUserId(List<String> userIdList);

    /**
     * ????????????
     */
    Integer total();

    /**
     * ??????????????????
     *
     * @param contentList
     */
    void bindContentData(List<Content> contentList);

    /**
     * ??????????????????
     *
     * @param radioList
     */
    void bindRadioContentData(List<Radio> radioList);


    /**
     * ????????????????????????
     *
     * @param content
     */
    void bindContentExtend(Content content);

    /**
     * ???????????????
     *
     * @param listContentDto
     */
    void bindContentMember(ListContentDto listContentDto);

    /**
     * ???????????????
     *
     * @param content
     */
    void bindContentMember(Content content);

    /**
     * ???????????????
     *
     * @param contentList
     */
    void bindContentMember(List<Content> contentList);


    Boolean currentUserIsAttentionAuthor(String contentUserId);

    /**
     * ??????????????????????????????
     *
     * @param contentId
     * @return
     */
    Content getManageContentDetail(String contentId);

    /**
     * ????????????
     *
     * @return
     */
    List<Content> list(Integer pageNum, Integer pageSize);

    /**
     * ?????????????????????
     *
     * @param targetId
     */
    void initContendData(String targetId);

    /**
     * ?????????????????????
     *
     * @param targetId
     */
    void viewNumPlusOne(String targetId);

    /**
     * ????????????????????????????????????
     *
     * @param pinnedStatus
     * @return
     */
    List<Content> getContentListByPinned(PinnedStatus pinnedStatus, String userId);


    void handlePinnedContent(ListContentDto listContentDto, String userId);

    ContentEditReason latestEditReason(String contentId);


    void bindContentEditReason(Content content);

    void bindContentStatement(Content content);

    /**
     * ?????????????????????
     *
     * @return
     */
    List<CountTagDto> listAllTag();

    List<CountTagDto> listCountByTagIds(List<String> tagIdList);

    /**
     * ??????????????????
     *
     * @param content
     */
    void bindLikesMember(Content content);

    /**
     * ?????????????????????
     *
     * @return
     */
    void lastAndNextContent(Content content);


    /**
     * ??????????????????
     *
     * @param updateStatusReq
     * @return
     */
    OperateContentDto updateContentStatus(UpdateStatusReq updateStatusReq);


    /**
     * ????????????
     *
     * @return
     */
    List<Content> randomContent(String notUserId);

    /**
     * ??????????????????
     *
     * @param userId
     * @return
     */
    Integer getUserTotalContentNum(String userId);

    void updateContentData(ContentData contentData);

    /**
     * ???????????????????????????
     *
     * @return
     * @note: ?????????15???
     */
    List<Content> latestContentList();

    /**
     * ????????????
     *
     * @param contentId
     * @return
     */
    Boolean exists(String contentId);

    void handleExistsDraft(ListContentDto listContentDto);
}
