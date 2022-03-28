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

package com.upupor.service.business.editor;

import com.upupor.service.business.aggregation.dao.entity.Content;
import com.upupor.service.business.aggregation.dao.entity.ContentExtend;
import com.upupor.service.business.aggregation.dao.entity.Member;
import com.upupor.service.business.aggregation.dao.entity.MemberConfig;
import com.upupor.service.business.aggregation.dao.mapper.MemberConfigMapper;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.outer.req.AddContentDetailReq;
import com.upupor.service.types.ContentIsInitialStatus;
import com.upupor.service.types.ContentStatus;
import com.upupor.service.utils.Asserts;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static com.upupor.framework.CcConstant.CvCache.createContentIntervalkey;
import static com.upupor.service.common.ErrorCode.CONTENT_NOT_EXISTS;
import static com.upupor.service.common.ErrorCode.MEMBER_CONFIG_LESS;


/**
 * @author Yang Runkang (cruise)
 * @date 2022年01月09日 11:20
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@Component
public class Create extends AbstractEditor<AddContentDetailReq> {
    @Resource
    private MemberConfigMapper memberConfigMapper;

    private Long limitedInterval;

    @Override
    protected EditorType editorType() {
        return EditorType.CREATE;
    }

    @Override
    protected void check() {
        Member member = getMember();
        limitedInterval = checkIntervalLimit(member.getUserId());
    }

    private Content createNewContent() {
        AddContentDetailReq addContentDetailReq = getReq();
        Member member = getMember();
        Content content = Content.create();
        generateContentId(content);
        content.setUserId(member.getUserId());
        content.setTitle(addContentDetailReq.getTitle());
        content.setContentType(addContentDetailReq.getContentType());
        content.setShortContent(addContentDetailReq.getShortContent());
        content.setTagIds(CcUtils.removeLastComma(addContentDetailReq.getTagIds()));
        content.setStatementId(member.getStatementId());
        // 初始化文章拓展表
        content.setContentExtend(ContentExtend.create(content.getContentId(), addContentDetailReq.getContent(), addContentDetailReq.getMdContent()));
        // 初始化文章数据
        contentService.initContendData(content.getContentId());
        // 原创处理
        originProcessing(content);
        // 具体操作 发布 或者 草稿
        publishOperator(content);
        return content;
    }

    @Override
    protected Boolean doBusiness() {
        Content content = createNewContent();
        int count = contentMapper.insert(content);
        int total = contentExtendMapper.insert(content.getContentExtend()) + count;
        boolean addSuccess = total > 1;

        if (addSuccess) {
            // 发送创建文章成功事件
            publishContentEvent(content);

            // 缓存创建文章的动作(标识),用于限制某一用户恶意刷文
            if (Objects.nonNull(limitedInterval)) {
                RedisUtil.set(createContentIntervalkey(getMember().getUserId()), content.getContentId(), limitedInterval);
            }
            // 更新索引
            updateIndex(content);
        }
        return addSuccess;
    }

    /**
     * 创建文章间隔时间检查
     *
     * @param userId
     * @return
     */
    private Long checkIntervalLimit(String userId) {
        Long timeCreateContentInterval = null;

        Integer hasMemberConfig = memberConfigMapper.countByUserId(userId);
        if (hasMemberConfig < 1) {
            return timeCreateContentInterval;
        }
        // 获取配置的发文限制
        MemberConfig memberConfig = memberService.memberInfoData(userId).getMemberConfig();
        timeCreateContentInterval = memberConfig.getIntervalTimeCreateContent();

        String limited = RedisUtil.get(createContentIntervalkey(userId));
        if (Objects.isNull(limited)) {
            return timeCreateContentInterval;
        }

        Asserts.notNull(memberConfig, MEMBER_CONFIG_LESS);
        timeCreateContentInterval = memberConfig.getIntervalTimeCreateContent();
        if (Objects.isNull(timeCreateContentInterval) || timeCreateContentInterval <= 0) {
            return timeCreateContentInterval;
        }

        LocalDateTime canCreateContentTime = LocalDateTime.now().plus(timeCreateContentInterval, ChronoUnit.SECONDS);
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        throw new BusinessException(ErrorCode.NEXT_PUBLIC_TIME, dtf2.format(canCreateContentTime), false);

    }

    private void publishOperator(Content content) {
        AddContentDetailReq addContentDetailReq = getReq();
        if (!StringUtils.isEmpty(addContentDetailReq.getOperation())) {
            String draftTag = "temp";
            if (draftTag.equals(addContentDetailReq.getOperation())) {
                content.setStatus(ContentStatus.DRAFT);
                content.setIsInitialStatus(ContentIsInitialStatus.NOT_FIRST_PUBLISH_EVER);
            }
        }
    }

    private void originProcessing(Content content) {
        AddContentDetailReq addContentDetailReq = getReq();
        if (Objects.nonNull(addContentDetailReq.getOriginType())) {
            content.setOriginType(addContentDetailReq.getOriginType());
            content.setNoneOriginLink(addContentDetailReq.getNoneOriginLink());
        }
    }


    private void generateContentId(Content content) {
        AddContentDetailReq addContentDetailReq = getReq();
        if (StringUtils.isEmpty(addContentDetailReq.getPreContentId())) {
            content.setContentId(CcUtils.getUuId());
        } else {
            // 检查preContentId是否已经使用
            checkPreContentId(addContentDetailReq);
            content.setContentId(addContentDetailReq.getPreContentId());
        }
    }


    private void checkPreContentId(AddContentDetailReq addContentDetailReq) {
        try {
            // 检查重复提交
            Content normalContent = contentService.getNormalContent(addContentDetailReq.getPreContentId());
            if (Objects.nonNull(normalContent)) {
                throw new BusinessException(ErrorCode.SUBMIT_REPEAT);
            }
        } catch (Exception e) {
            if (e instanceof BusinessException && ((BusinessException) e).getCode().equals(CONTENT_NOT_EXISTS.getCode())) {
                // 忽略
            } else {
                throw new BusinessException(ErrorCode.UNKNOWN_EXCEPTION);
            }
        }
    }

    @Override
    protected void updateIndex(Content content) {
        try {
            if (ContentStatus.NORMAL.equals(content.getStatus())) {
                upuporLuceneService.addDocument(content.getTitle(), content.getContentId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建文章,添加索引失败,contentId:" + content.getContentId() + ",文章标题:" + content.getTitle());
        }
    }
}
