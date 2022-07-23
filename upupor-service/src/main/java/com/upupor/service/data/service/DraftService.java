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

import com.upupor.service.data.dao.entity.Draft;
import com.upupor.service.dto.dao.ListDraftDto;
import com.upupor.service.outer.req.content.AutoSaveContentReq;

import java.util.List;

/**
 * 草稿服务
 *
 * @author Yang Runkang (cruise)
 * @createTime 2022-07-20 00:26
 * @email: yangrunkang53@gmail.com
 */
public interface DraftService {
    /**
     * 创建草稿
     *
     * @param draft
     * @return
     */
    Boolean create(Draft draft);

    /**
     * 更新草稿
     *
     * @param draft
     * @return
     */
    Boolean update(Draft draft);

    /**
     * 删除草稿
     *
     * @param id
     * @return
     */
    Boolean delete(Long id);

    /**
     * 获取草稿
     *
     * @param listDraftDto
     * @return
     */
    List<Draft> listByDto(ListDraftDto listDraftDto);

    Draft getByDraftId(String draftId);

    Draft getByDraftIdAndUserId(String draftId, String userId);

    Boolean autoSaveContent(AutoSaveContentReq autoSaveContentReq);

    /**
     * 草稿总数
     *
     * @param userId
     * @return
     */
    Long countDraft(String userId);
}
