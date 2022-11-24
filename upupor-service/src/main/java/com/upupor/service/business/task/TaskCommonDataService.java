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

package com.upupor.service.business.task;

import com.upupor.framework.CcConstant;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.dao.entity.Radio;
import com.upupor.service.data.service.ContentService;
import com.upupor.service.data.service.MemberService;
import com.upupor.service.data.service.RadioService;
import com.upupor.service.dto.page.common.ListMemberDto;
import com.upupor.service.dto.page.common.ListRadioDto;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务数据公共服务
 *
 * @author Yang Runkang (cruise)
 * @date 2022年03月27日 09:53
 * @email: yangrunkang53@gmail.com
 */
@Service
public class TaskCommonDataService {

    @Resource
    private ContentService contentService;

    @Resource
    private MemberService memberService;

    @Resource
    private RadioService radioService;

    public List<Content> contentList() {

        Integer total = contentService.total();

        int pageNum = total % CcConstant.Page.SIZE == 0 ? total / CcConstant.Page.SIZE : total / CcConstant.Page.SIZE + 1;
        List<Content> contentList = new ArrayList<>();
        for (int i = 0; i < pageNum; i++) {
            List<Content> contents = contentService.list(i + 1, CcConstant.Page.SIZE);
            contentList.addAll(contents);
        }

        return contentList;
    }


    public List<Member> memberList() {
        Integer total = memberService.total();
        int pageNum = total % CcConstant.Page.SIZE == 0 ? total / CcConstant.Page.SIZE : total / CcConstant.Page.SIZE + 1;
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < pageNum; i++) {
            ListMemberDto listMemberDto = memberService.list(i + 1, CcConstant.Page.SIZE);
            if (CollectionUtils.isEmpty(listMemberDto.getMemberList())) {
                continue;
            }
            memberList.addAll(listMemberDto.getMemberList());
        }

        return memberList;
    }


    public List<Radio> radioList() {
        Integer total = radioService.total();
        int pageNum = total % CcConstant.Page.SIZE == 0 ? total / CcConstant.Page.SIZE : total / CcConstant.Page.SIZE + 1;
        List<Radio> radioAllList = new ArrayList<>();
        for (int i = 0; i < pageNum; i++) {
            ListRadioDto listRadioDto = radioService.list(i + 1, CcConstant.Page.SIZE);
            if (CollectionUtils.isEmpty(listRadioDto.getRadioList())) {
                break;
            }
            radioAllList.addAll(listRadioDto.getRadioList());
        }
        return radioAllList;
    }

}
