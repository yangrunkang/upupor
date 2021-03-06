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
 * ??????????????????????????????
 *
 * @author YangRunkang(cruise)
 * @date 2020/04/22 01:19
 */
@Slf4j
@Api(tags = "??????????????????????????????")
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
     * ???????????????????????????????????????
     */
    @ApiOperation("???????????????????????????????????????")
    @GetMapping("/editor")
    public ModelAndView editor(@RequestParam(value = "type", required = false) ContentType contentType, // ??????&??????
                               @RequestParam(value = "contentId", required = false) String contentId, // ?????????????????????
                               @RequestParam(value = "tag", required = false) String tag // ????????????????????????????????????tag
    ) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDITOR);
        modelAndView.addObject(SeoKey.TITLE, "?????????");

        if (Objects.isNull(contentType)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "??????????????????????????????");
        }

        try {

            // ??????????????????
            GetEditorReq getEditorReq = new GetEditorReq();
            getEditorReq.setContentType(contentType);
            getEditorReq.setTag(tag);
            EditorIndexDto editorIndexDto = editorAggregateService.index(getEditorReq);

            // ??????????????????
            if (Objects.nonNull(contentId)) {
                Draft draft = draftService.getByDraftId(contentId);
                if (Objects.nonNull(draft)) {
                    Content content = Draft.parseContent(draft);

                    // ?????????????????????????????????
                    {
                        Content queryContent = null;
                        try {
                            queryContent = contentService.getManageContentDetail(contentId);
                        } catch (Exception e) {
                            if (!(e instanceof BusinessException && (((BusinessException) e).getCode().equals(CONTENT_NOT_EXISTS.getCode())))) {
                                throw new BusinessException(ErrorCode.UNKNOWN_EXCEPTION);
                            }
                        }
                        boolean hasContentEmpty = Objects.nonNull(queryContent);
                        if (hasContentEmpty) {
                            content.setStatus(queryContent.getStatus()); // ??????????????????????????????
                        }
                        modelAndView.addObject(HAS_DRAFT, hasContentEmpty);
                    }


                    editorIndexDto.setContent(content);
                } else {
                    setDbContent(contentId, editorIndexDto);
                }
            }

            modelAndView.addObject(editorIndexDto);
            // ????????????
            modelAndView.addObject(CONTENT_TYPE, contentType);
            // ????????????????????????????????????ID,??????????????????????????????
            if (StringUtils.isEmpty(contentId)) {
                // ????????? ??????ID,??????????????????,????????????????????????id????????????id
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


    private void setDbContent(String contentId, EditorIndexDto editorIndexDto) {
        Content content = contentService.getManageContentDetail(contentId);
        if (Objects.isNull(content)) {
            throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
        }
        // ????????????????????????????????????
        if (!content.getUserId().equals(ServletUtils.getUserId())) {
            throw new BusinessException(ErrorCode.BAN_EDIT_OTHERS_CONTENT);
        }
        editorIndexDto.setContent(content);
    }

}
