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
import com.upupor.service.dto.page.EditorIndexDto;
import com.upupor.service.outer.req.GetEditorReq;
import com.upupor.service.types.ContentType;
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

    /**
     * 正常流程从页面新建编辑内容
     */
    @ApiOperation("正常流程从页面新建编辑内容")
    @GetMapping("/editor")
    public ModelAndView editor(@RequestParam(value = "type", required = false) ContentType contentType, // 新增&编辑
                               @RequestParam(value = "contentId", required = false) String contentId, // 编辑指定的文章
                               @RequestParam(value = "edit", required = false) Boolean edit, // 是否是编辑已有文章
                               @RequestParam(value = "tag", required = false) String tag // 快捷操作新增文章会到指定tag
    ) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDITOR);
        modelAndView.addObject(SeoKey.TITLE, "编辑器");

        if (Objects.isNull(contentType)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "从未知入口进入编辑器");
        }

        try {
            GetEditorReq getEditorReq = new GetEditorReq();
            getEditorReq.setContentType(contentType);
            getEditorReq.setTag(tag);
            getEditorReq.setContentId(contentId);
            EditorIndexDto editorIndexDto = editorAggregateService.index(getEditorReq);
            modelAndView.addObject(editorIndexDto);
            // 如果是从管理后台过来的编辑,edit为true,然后依次来控制按钮是否显示或者隐藏
            modelAndView.addObject("edit", edit);
            // 参数传递
            modelAndView.addObject("type", contentType);

            // 创建新的内容会使用预生成ID,防止页面表单重复提交
            if (StringUtils.isEmpty(contentId)) {
                // 预生成 内容ID,防止重复提交,默认使用预生成的id作为文章id
                modelAndView.addObject("pre_content_id", CcUtils.getUuId());
                // 新文章,如果是新文章,可以使用pre_content_id去update草稿内容
                modelAndView.addObject("new_content_tag", Boolean.TRUE);
            } else {
                modelAndView.addObject("new_content_tag", Boolean.FALSE);
            }
        } catch (Exception e) {
            modelAndView.addObject(CcConstant.GLOBAL_EXCEPTION, e.getMessage());
            modelAndView.setViewName(PAGE_500);
        }

        return modelAndView;
    }

}
