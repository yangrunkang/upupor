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

package com.upupor.service.business.lucene;

import com.upupor.lucene.UpuporLuceneService;
import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.service.business.task.TaskCommonDataService;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.dao.entity.Radio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年11月24日
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@Service
public class LuceneService {
    @Resource
    private TaskCommonDataService scheduledCommonService;

    @Resource
    private UpuporLuceneService upuporLuceneService;

    public void init() {
        initContentLucene();
        initRadioLucene();
        initMemberLucene();
    }

    private void initMemberLucene() {
        List<Member> memberList = scheduledCommonService.memberList();
        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }

        for (Member member : memberList) {
            upuporLuceneService.addDocument(member.getUserName(), member.getUserId(), LuceneDataType.MEMBER);
        }

        log.info("初始化用户索引完成...........");
    }

    private void initRadioLucene() {
        List<Radio> radioList = scheduledCommonService.radioList();
        if (CollectionUtils.isEmpty(radioList)) {
            return;
        }
        for (Radio radio : radioList) {
            upuporLuceneService.addDocument(radio.getRadioIntro(), radio.getRadioId(), LuceneDataType.RADIO);
        }

        log.info("初始化电台索引完成...........");


    }

    private void initContentLucene() {
        List<Content> contentList = scheduledCommonService.contentList();
        if (CollectionUtils.isEmpty(contentList)) {
            return;
        }

        for (Content content : contentList) {
            upuporLuceneService.addDocument(content.getTitle(), content.getContentId(), LuceneDataType.CONTENT);
        }

        log.info("初始化文章内容索引完成...........");
    }
}
