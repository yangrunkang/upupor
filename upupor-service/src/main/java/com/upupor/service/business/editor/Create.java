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

import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.ContentExtend;
import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.MemberConfig;
import com.upupor.data.dao.entity.converter.Converter;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dao.mapper.MemberConfigMapper;
import com.upupor.data.dto.OperateContentDto;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcUtils;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.service.listener.event.PublishContentEvent;
import com.upupor.service.outer.req.content.CreateContentReq;
import com.upupor.service.utils.Asserts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static com.upupor.framework.CcRedisKey.createContentIntervalKey;
import static com.upupor.framework.ErrorCode.CONTENT_NOT_EXISTS;
import static com.upupor.framework.ErrorCode.MEMBER_CONFIG_LESS;


/**
 * @author Yang Runkang (cruise)
 * @date 2022年01月09日 11:20
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@Component
public class Create extends AbstractEditor<CreateContentReq> {
    @Resource
    private MemberConfigMapper memberConfigMapper;

    private Long limitedInterval;

    @Override
    protected EditorType editorType() {
        return EditorType.CREATE;
    }

    @Override
    protected void check() {
        Member member = getMember().getMember();
        limitedInterval = checkIntervalLimit(member.getUserId());
    }

    private ContentEnhance createNewContent() {
        CreateContentReq createContentReq = getReq();
        Member member = getMember().getMember();
        String contentId = generateContentId();
        ContentEnhance contentEnhance = create(contentId, createContentReq);
        Content content = contentEnhance.getContent();
        content.setUserId(member.getUserId());
        content.setStatementId(member.getStatementId());
        contentService.initContendData(content.getContentId());
        return contentEnhance;
    }

    @Override
    protected OperateContentDto doBusiness() {
        ContentEnhance contentEnhance = createNewContent();
        boolean addSuccess = contentService.insertContent(contentEnhance);
        Content content = contentEnhance.getContent();

        if (addSuccess) {
            // 发送创建文章成功事件
            publishContentEvent(content);

            // 缓存创建文章的动作(标识),用于限制某一用户恶意刷文
            if (Objects.nonNull(limitedInterval)) {
                RedisUtil.set(createContentIntervalKey(getMember().getMember().getUserId()), content.getContentId(), limitedInterval);
            }
        }

        return OperateContentDto.builder()
                .contentId(content.getContentId())
                .success(addSuccess)
                .status(content.getStatus())
                .build();
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

        String limited = RedisUtil.get(createContentIntervalKey(userId));
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

    private String generateContentId() {
        CreateContentReq createContentReq = getReq();

        if (StringUtils.isEmpty(createContentReq.getPreContentId())) {
            return CcUtils.getUuId();
        } else {
            // 检查preContentId是否已经使用
            try {
                // 检查重复提交
                ContentEnhance normalContent = contentService.getNormalContent(createContentReq.getPreContentId());
                if (Objects.nonNull(normalContent)) {
                    throw new BusinessException(ErrorCode.SUBMIT_REPEAT);
                }
            } catch (Exception e) {
                if (!(e instanceof BusinessException && (
                        ((BusinessException) e).getCode().equals(CONTENT_NOT_EXISTS.getCode())
                                ||
                                ((BusinessException) e).getCode().equals(ErrorCode.SUBMIT_REPEAT.getCode())
                ))) {
                    throw new BusinessException(ErrorCode.UNKNOWN_EXCEPTION);
                }
            }

            return createContentReq.getPreContentId();
        }
    }

    /**
     * 发布文章事件
     *
     * @param content
     */
    protected void publishContentEvent(Content content) {
        PublishContentEvent createContentEvent = new PublishContentEvent();
        createContentEvent.setContentId(content.getContentId());
        createContentEvent.setUserId(content.getUserId());
        eventPublisher.publishEvent(createContentEvent);
    }

    public static ContentEnhance create(String contentId, CreateContentReq createContentReq) {
        Content content = new Content();
        content.setContentId(contentId);
        content.setTitle(createContentReq.getTitle());
        content.setContentType(createContentReq.getContentType());
        content.setShortContent(createContentReq.getShortContent());
        content.setTagIds(CcUtils.removeLastComma(createContentReq.getTagIds()));
        // 原创处理
        if (Objects.nonNull(createContentReq.getOriginType())) {
            content.setOriginType(createContentReq.getOriginType());
            content.setNoneOriginLink(createContentReq.getNoneOriginLink());
        }
        return Converter.contentEnhance(content, ContentExtend.create(
                contentId,
                createContentReq.getContent(),
                createContentReq.getMdContent()
        ));
    }
}
