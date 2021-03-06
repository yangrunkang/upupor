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

package com.upupor.service.business.editor;

import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.data.dao.entity.Draft;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.dao.mapper.ContentExtendMapper;
import com.upupor.service.data.dao.mapper.ContentMapper;
import com.upupor.service.data.service.ContentService;
import com.upupor.service.data.service.DraftService;
import com.upupor.service.data.service.MemberService;
import com.upupor.service.dto.OperateContentDto;
import com.upupor.service.outer.req.content.BaseContentReq;
import com.upupor.service.utils.ServletUtils;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * ????????????
 *
 * @author Yang Runkang (cruise)
 * @date 2022???01???09??? 11:13
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractEditor<T extends BaseContentReq> {

    @Resource
    protected MemberService memberService;

    @Resource
    protected ContentExtendMapper contentExtendMapper;

    @Resource
    protected ContentMapper contentMapper;

    protected ContentService contentService;

    @Resource
    protected DraftService draftService;

    @Resource
    protected ApplicationEventPublisher eventPublisher;


    protected T req;

    public T getReq() {
        return req;
    }

    /**
     * ????????????
     */
    public enum EditorType {
        /**
         * ????????????
         */
        CREATE,

        /**
         * ????????????
         */
        EDIT,

        /**
         * ????????????
         */
        UPDATE_STATUS,
    }

    public Member getMember() {
        return memberService.memberInfo(ServletUtils.getUserId());
    }

    /**
     * ??????????????????
     *
     * @return
     */
    protected abstract EditorType editorType();


    /**
     * ??????
     */
    protected abstract void check();

    /**
     * ????????????
     */
    protected abstract OperateContentDto doBusiness();


    /**
     * ????????????
     */
    public static OperateContentDto execute(List<AbstractEditor> abstractEditorList, EditorType editorType, BaseContentReq baseContentReq) {
        for (AbstractEditor abstractEditor : abstractEditorList) {
            if (abstractEditor.editorType().equals(editorType)) {
                abstractEditor.contentService = SpringContextUtils.getBean(ContentService.class);
                abstractEditor.req = baseContentReq;
                abstractEditor.check();
                OperateContentDto operateContentDto = abstractEditor.doBusiness();
                if (operateContentDto.getSuccess()) {
                    // ????????????
                    String preContentId = baseContentReq.getPreContentId();
                    Draft draft = abstractEditor.draftService.getByDraftId(preContentId);
                    if (Objects.nonNull(draft)) {
                        abstractEditor.draftService.delete(draft.getId());
                    }
                }
                return operateContentDto;
            }
        }
        throw new BusinessException(ErrorCode.NOT_EXISTS_ABSTRACT_EDITOR_IMPLEMENTS);
    }


}
