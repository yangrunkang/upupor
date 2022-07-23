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

package com.upupor.web.page;

import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.data.aggregation.EditorAggregateService;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.dao.entity.Draft;
import com.upupor.service.data.service.ContentService;
import com.upupor.service.data.service.DraftService;
import com.upupor.service.dto.page.EditorIndexDto;
import com.upupor.service.outer.req.GetEditorReq;
import com.upupor.service.types.ContentType;
import com.upupor.service.utils.ServletUtils;
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
    private final static String HAS_DRAFT = "hasContentCanToRestore";


    /**
     * 正常流程从页面新建编辑内容
     */
    @ApiOperation("正常流程从页面新建编辑内容")
    @GetMapping("/editor")
    public ModelAndView editor(@RequestParam(value = "type", required = false) ContentType contentType, // 新增&编辑
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

            // 处理文章信息
            if (Objects.nonNull(contentId)) {
                Draft draft = draftService.getByDraftId(contentId);

                if (Objects.nonNull(draft)) {
                    editorIndexDto.setContent(Draft.parseContent(contentId, draft.getDraftContent(), draft.getUserId()));
                    modelAndView.addObject(HAS_DRAFT, hasContentCanToRestore(contentId));
                } else {
                    setDbContent(contentId, editorIndexDto);
                }
            }

            modelAndView.addObject(editorIndexDto);
            // 参数传递
            modelAndView.addObject(CONTENT_TYPE, contentType);
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

    /**
     * 是否有正式的内容可以重置
     *
     * @param contentId
     * @return
     */
    private boolean hasContentCanToRestore(String contentId) {
        Content content = null;
        try {
            content = contentService.getManageContentDetail(contentId);
        } catch (Exception e) {
            if (!(e instanceof BusinessException && ((BusinessException) e).getCode().equals(CONTENT_NOT_EXISTS.getCode()))) {
                throw e;
            }
        }
        return Objects.nonNull(content);
    }

    private void setDbContent(String contentId, EditorIndexDto editorIndexDto) {
        Content content = contentService.getManageContentDetail(contentId);
        if (Objects.isNull(content)) {
            throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
        }
        // 校验文章是否是作者本人的
        if (!content.getUserId().equals(ServletUtils.getUserId())) {
            throw new BusinessException(ErrorCode.BAN_EDIT_OTHERS_CONTENT);
        }
        editorIndexDto.setContent(content);
    }

}
