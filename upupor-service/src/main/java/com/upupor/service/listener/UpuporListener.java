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

package com.upupor.service.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.data.dao.entity.Comment;
import com.upupor.data.dao.entity.ContentExtend;
import com.upupor.data.dao.entity.Draft;
import com.upupor.data.dao.mapper.CommentMapper;
import com.upupor.data.dao.mapper.ContentExtendMapper;
import com.upupor.data.dao.mapper.DraftMapper;
import com.upupor.framework.CcConstant;
import com.upupor.framework.CcRedis;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.service.base.MemberService;
import com.upupor.service.business.lucene.LuceneService;
import com.upupor.service.business.task.TaskService;
import com.upupor.service.listener.event.BuriedPointDataEvent;
import com.upupor.service.listener.event.CompressContentEvent;
import com.upupor.service.listener.event.InitLuceneIndexEvent;
import com.upupor.service.listener.event.InitSensitiveWordEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;


/**
 * Upupor 监听器
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/18 11:47
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class UpuporListener {
    private final MemberService memberService;
    private final TaskService taskService;
    private final LuceneService luceneService;
    private final ContentExtendMapper contentExtendMapper;
    private final DraftMapper draftMapper;
    private final CommentMapper commentMapper;

    private static final String ZIPPED = "zipped";
    private static final String ZIP_KEY = "zip";

    /**
     * 埋点
     *
     * @param event
     */
    @EventListener
    @Async
    public void buriedPointData(BuriedPointDataEvent event) {
        HttpServletRequest request = event.getRequest();
        HttpSession session = request.getSession();

        Object userIdAttribute = session.getAttribute(CcConstant.Session.USER_ID);
        if (Objects.isNull(userIdAttribute)) {
            return;
        }

        try {
            String userId = String.valueOf(userIdAttribute);
            //检测是否是userId
            if (memberService.exists(userId)) {
                // 更新token过期时间
                CcRedis.Operate.updateTokenExpireTime(userId);
                // 延长活跃Key
                CcRedis.Operate.memberActive(userId);
                // 刷新用户活跃时间
                refreshUserActiveTime(userId);
            }
        } catch (Exception ignore) {
            
        }
    }

    private void refreshUserActiveTime(String userId) {
        String userActiveKey = CcRedis.Key.userActiveKey(userId);
        RedisUtil.set(userActiveKey, String.valueOf(CcDateUtil.getCurrentTime()));
    }

    @EventListener
    @Async
    public void initLuceneIndexEvent(InitLuceneIndexEvent event) {
        // 采用的是内存存储,目的是尽量的减少依赖包括文件依赖;不使用ES,原因是ES太重
        log.info("开始初始化Lucene索引....");
        luceneService.init();
    }

    @EventListener
    @Async
    public void initSensitiveWordEvent(InitSensitiveWordEvent event) {
        log.info("开始初始化敏感词....");
        taskService.refreshSensitiveWord();
    }

    @EventListener
    @Async
    public void compressContent(CompressContentEvent event) {
        zip();
    }

    private void zip() {
        if (ZIPPED.equals(RedisUtil.get(ZIP_KEY))) {
            return;
        }
        zipContent();
        zipDraft();
        zipComment();

        RedisUtil.set(ZIP_KEY, ZIPPED);
    }

    private void zipComment() {
        LambdaQueryWrapper<Comment> listQuery = new LambdaQueryWrapper<Comment>();
        int pageNum = 1, pageSize = 100;
        PageInfo<Comment> pageInfo = null;
        do {
            PageHelper.startPage(pageNum, pageSize);
            List<Comment> contents = commentMapper.selectList(listQuery);
            pageInfo = new PageInfo<>(contents);

            for (Comment comment : pageInfo.getList()) {
                comment.zip();
                commentMapper.updateById(comment);
            }
            pageNum = pageInfo.getNextPage();
        } while (!pageInfo.isIsLastPage());
    }

    private void zipDraft() {
        LambdaQueryWrapper<Draft> listQuery = new LambdaQueryWrapper<Draft>();
        int pageNum = 1, pageSize = 100;
        PageInfo<Draft> pageInfo = null;
        do {
            PageHelper.startPage(pageNum, pageSize);
            List<Draft> contents = draftMapper.selectList(listQuery);
            pageInfo = new PageInfo<>(contents);

            for (Draft draft : pageInfo.getList()) {
                draft.zip();
                draftMapper.updateById(draft);
            }
            pageNum = pageInfo.getNextPage();
        } while (!pageInfo.isIsLastPage());
    }

    private void zipContent() {
        LambdaQueryWrapper<ContentExtend> listQuery = new LambdaQueryWrapper<ContentExtend>();
        int pageNum = 1, pageSize = 100;
        PageInfo<ContentExtend> pageInfo = null;
        do {
            PageHelper.startPage(pageNum, pageSize);
            List<ContentExtend> contents = contentExtendMapper.selectList(listQuery);
            pageInfo = new PageInfo<>(contents);

            for (ContentExtend contentExtend : pageInfo.getList()) {
                contentExtend.zip();
                contentExtendMapper.updateById(contentExtend);
            }
            pageNum = pageInfo.getNextPage();
        } while (!pageInfo.isIsLastPage());
    }

}
