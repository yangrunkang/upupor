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

import com.upupor.data.dao.entity.Draft;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.dao.mapper.ContentExtendMapper;
import com.upupor.data.dao.mapper.ContentMapper;
import com.upupor.data.dto.OperateContentDto;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.ServletUtils;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.base.ContentService;
import com.upupor.service.base.DraftService;
import com.upupor.service.base.MemberService;
import com.upupor.service.outer.req.content.BaseContentReq;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 编辑抽象
 *
 * @author Yang Runkang (cruise)
 * @date 2022年01月09日 11:13
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
     * 编辑类型
     */
    public enum EditorType {
        /**
         * 创建文章
         */
        CREATE,

        /**
         * 编辑文章
         */
        EDIT,

        /**
         * 更新状态
         */
        UPDATE_STATUS,
    }

    public MemberEnhance getMember() {
        return memberService.memberInfo(ServletUtils.getUserId());
    }

    /**
     * 编辑类型类型
     *
     * @return
     */
    protected abstract EditorType editorType();


    /**
     * 校验
     */
    protected abstract void check();

    /**
     * 执行业务
     */
    protected abstract OperateContentDto doBusiness();


    /**
     * 执行业务
     */
    public static OperateContentDto execute(List<AbstractEditor> abstractEditorList, EditorType editorType, BaseContentReq baseContentReq) {
        for (AbstractEditor abstractEditor : abstractEditorList) {
            if (abstractEditor.editorType().equals(editorType)) {
                abstractEditor.contentService = SpringContextUtils.getBean(ContentService.class);
                abstractEditor.req = baseContentReq;
                abstractEditor.check();
                OperateContentDto operateContentDto = abstractEditor.doBusiness();
                if (operateContentDto.getSuccess()) {
                    // 移除草稿
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
