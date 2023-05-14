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

package com.upupor.web.page.controller;

import com.upupor.data.dao.entity.Draft;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dto.page.EditorIndexDto;
import com.upupor.data.types.ContentType;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.aggregation.EditorAggregateService;
import com.upupor.service.base.ContentService;
import com.upupor.service.base.DraftService;
import com.upupor.service.outer.req.GetEditorReq;
import com.upupor.service.utils.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import java.util.Objects;

import static com.upupor.framework.CcConstant.*;
import static com.upupor.framework.ErrorCode.CONTENT_NOT_EXISTS;


/**
 * 编辑器页面跳转控制器
 *
 * @author YangRunkang(cruise)
 * @date 2020/04/22 01:19
 */
@Slf4j
@Api(tags = "编辑器页面跳转控制器")
@RestController
@RequiredArgsConstructor
public class EditorPageJumpController {

    private final EditorAggregateService editorAggregateService;
    private final ContentService contentService;
    private final DraftService draftService;

    private final static String CONTENT_TYPE = "type";
    private final static String PRE_CONTENT_ID = "pre_content_id";
    private final static String EDIT_BTN_TEXT = "edit_btn_text";
    private final static String ALREADY_HAS_DRAFT = "hasContentCanToRestore";


    /**
     * 正常流程从页面新建编辑内容
     */
    @ApiOperation("正常流程从页面新建编辑内容")
    @GetMapping("/editor")
    public ModelAndView editor(@RequestParam(value = "type", required = false) ContentType contentType, // 内容类型
                               @RequestParam(value = "contentId", required = false) String contentId, // 编辑指定的文章
                               @RequestParam(value = "tag", required = false) String tag // 快捷操作新增文章会到指定tag
    ) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDITOR);
        modelAndView.addObject(SeoKey.TITLE, "编辑器");

        if (Objects.isNull(contentType)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "从未知入口进入编辑器");
        }

        try {

            // 获取基础信息
            GetEditorReq getEditorReq = new GetEditorReq();
            getEditorReq.setContentType(contentType);
            getEditorReq.setTag(tag);

            EditorIndexDto editorIndexDto = editorAggregateService.index(getEditorReq);
            modelAndView.addObject(EDIT_BTN_TEXT, "发布");
            modelAndView.addObject(CONTENT_TYPE, contentType);

            // 处理文章信息
            if (Objects.nonNull(contentId)) {
                Draft draft = draftService.getByDraftId(contentId);
                if (Objects.isNull(draft)) {
                    contentEdit(contentId, editorIndexDto);
                    modelAndView.addObject(EDIT_BTN_TEXT, "编辑完成");
                } else {
                    draftEdit(contentId, modelAndView, editorIndexDto, draft);
                    modelAndView.addObject(EDIT_BTN_TEXT, "发布");
                }
            }
            modelAndView.addObject(editorIndexDto);

            // 创建新的内容会使用预生成ID,防止页面表单重复提交
            if (StringUtils.isEmpty(contentId)) {
                // 预生成 内容ID,防止重复提交,默认使用预生成的id作为文章id
                modelAndView.addObject(PRE_CONTENT_ID, CcUtils.getUuId());
            } else {
                modelAndView.addObject(PRE_CONTENT_ID, contentId);
            }
        } catch (Exception e) {
            modelAndView.addObject(CcConstant.GLOBAL_EXCEPTION, e.getMessage());
            modelAndView.setViewName(PAGE_500);
        }

        return modelAndView;
    }

    private void draftEdit(String contentId, ModelAndView modelAndView, EditorIndexDto editorIndexDto, Draft draft) {
        ContentEnhance contentEnhanceDraft = Draft.parseContent(draft);

        // 查看是否有非草稿的文章
        {
            ContentEnhance dbContentEnhance = null;
            try {
                dbContentEnhance = contentService.getManageContentDetail(contentId);
            } catch (Exception e) {
                if (!(e instanceof BusinessException && (((BusinessException) e).getCode().equals(CONTENT_NOT_EXISTS.getCode())))) {
                    throw new BusinessException(ErrorCode.UNKNOWN_EXCEPTION);
                }
            }
            boolean hasContent = Objects.nonNull(dbContentEnhance);
            if (hasContent) {
                contentEnhanceDraft.getContent().setStatus(dbContentEnhance.getContent().getStatus()); // 覆盖非草稿文章的状态
            }
            modelAndView.addObject(ALREADY_HAS_DRAFT, hasContent);
        }

        editorIndexDto.setContentEnhance(contentEnhanceDraft);
    }


    private void contentEdit(String contentId, EditorIndexDto editorIndexDto) {
        ContentEnhance contentEnhance = contentService.getManageContentDetail(contentId);
        if (Objects.isNull(contentEnhance)) {
            throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
        }
        // 校验文章是否是作者本人的
        if (!contentEnhance.getContent().getUserId().equals(SessionUtils.getUserId())) {
            throw new BusinessException(ErrorCode.BAN_EDIT_OTHERS_CONTENT);
        }
        editorIndexDto.setContentEnhance(contentEnhance);
    }

}
