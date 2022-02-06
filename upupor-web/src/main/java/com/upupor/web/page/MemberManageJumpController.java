/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
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

package com.upupor.web.page;

import com.upupor.service.business.manage.AbstractManageInfoGet;
import com.upupor.service.business.manage.ManageDto;
import com.upupor.service.business.manage.business.ApplyCommitManage;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

import static com.upupor.framework.CcConstant.Page;
import static com.upupor.framework.CcConstant.SeoKey;


/**
 * 用户页面跳转控制器
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/19 12:16
 */
@Slf4j
@Api(tags = "用户页面跳转控制器")
@RestController
@RequiredArgsConstructor
public class MemberManageJumpController {

    private final List<AbstractManageInfoGet> abstractManageInfoGetList;
    private final ApplyCommitManage applyCommitManage;

    @ApiOperation("个人中心-内容管理")
    @GetMapping("/user/manage/{path}")
    public ModelAndView userManagePage(@PathVariable(value = "path") String path, String messageStatus, Integer pageNum, Integer pageSize, String searchTitle, String select) {
        if (Objects.isNull(pageNum)) {
            pageNum = Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = Page.SIZE;
            if ("content".equals(path) || "draft".equals(path)) {
                // 如果标题不为空,默认搜索出300条数据
                if (!StringUtils.isEmpty(searchTitle)) {
                    pageSize = Page.MAX_SIZE;
                }
            }
        }

        for (AbstractManageInfoGet abstractManageInfoGet : abstractManageInfoGetList) {
            ManageDto build = ManageDto.builder().pageSize(pageSize).pageNum(pageNum).userId(ServletUtils.getUserId()).searchTitle(searchTitle).messageStatus(messageStatus).select(select).build();
            if (abstractManageInfoGet.viewName().replace(abstractManageInfoGet.prefix(), Strings.EMPTY).equals(path)) {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.setViewName(abstractManageInfoGet.viewName());
                modelAndView.addObject(abstractManageInfoGet.getData(build));
                modelAndView.addObject(SeoKey.TITLE, abstractManageInfoGet.viewDesc());
                modelAndView.addObject(SeoKey.DESCRIPTION, abstractManageInfoGet.viewDesc());
                return modelAndView;
            }
        }

        throw new BusinessException(ErrorCode.NONE_PAGE);
    }


    @ApiOperation("个人中心-消息")
    @GetMapping("/user/manage/{path}/{messageStatus}")
    public ModelAndView message(@PathVariable(value = "path") String path, @PathVariable(value = "messageStatus", required = false) String messageStatus, Integer pageNum, Integer pageSize) {
       return userManagePage(path,messageStatus,pageNum,pageSize,null,null);
    }


    @ApiOperation("个人中心-申请管理-提交申请")
    @GetMapping("/user/manage/apply/commit/{applyId}")
    public ModelAndView userManageApplyCommit(@PathVariable String applyId) {
        ManageDto build = ManageDto.builder()
                .applyId(applyId)
                .userId(ServletUtils.getUserId())
                .build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(applyCommitManage.viewName());
        modelAndView.addObject(applyCommitManage.getData(build));
        modelAndView.addObject(SeoKey.TITLE, applyCommitManage.viewDesc());
        modelAndView.addObject(SeoKey.DESCRIPTION,  applyCommitManage.viewDesc());
        return modelAndView;
    }


}
