package com.upupor.service.business.aggregation;

import com.upupor.service.business.aggregation.service.*;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dto.page.ContentIndexDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.dto.page.common.TagDto;
import com.upupor.spi.req.ListContentReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 内容数据集合
 *
 * @author runkangyang (cruise)
 * @date 2020.01.11 11:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentAggregateService {

    private final ContentService contentService;
    private final CommentService commentService;
    private final CollectService collectService;
    private final TagService tagService;
    private final ViewerService viewerService;

    /**
     * 文章详情
     *
     * @param contentId
     * @return
     */
    public ContentIndexDto contentDetail(String contentId, Integer pageNum, Integer pageSize) {
        // 获取文章内容
        Content content = contentService.getContentDetail(contentId);
        return commonContentIndexDto(pageNum, pageSize, content);
    }

    /**
     * 文章详情
     *
     * @param contentId
     * @return
     */
    public ContentIndexDto contentManageDetail(String contentId, Integer pageNum, Integer pageSize) {
        // 获取文章内容
        Content content = contentService.getManageContentDetail(contentId);
        return commonContentIndexDto(pageNum, pageSize, content);
    }

    /**
     * 正常文章和草稿文章
     *
     * @param pageNum
     * @param pageSize
     * @param content
     * @return
     */
    private ContentIndexDto commonContentIndexDto(Integer pageNum, Integer pageSize, Content content) {

        if (content.getStatus().equals(CcEnum.ContentStatus.DELETED.getStatus())) {
            throw new BusinessException(ErrorCode.ARTICLE_DELETED);
        }

        // 获取文章数据
        contentService.bindContentData(Collections.singletonList(content));

        // 绑定文章作者
        contentService.bindContentMember(content);

        // 获取评论信息
        commentService.bindContentComment(content, pageNum, pageSize);

        // 绑定文章声明
        contentService.bindContentStatement(content);

        // 绑定最近编辑的原因
        contentService.bindContentEditReason(content);

        // 绑定文章收藏数
        handleContentCollectNum(content);

        // 获取文章标签
        List<TagDto> tagDtoList = tagService.listTagNameByTagId(content.getTagIds());

        // 绑定点赞的人
        contentService.bindLikesMember(content);

        // 上一篇下一篇
        contentService.lastAndNextContent(content);

        // 记录访问者
        content.setViewerList(viewerService.listViewerByTargetIdAndType(content.getContentId(), CcEnum.ViewTargetType.CONTENT));

        ContentIndexDto contentIndexDto = new ContentIndexDto();
        contentIndexDto.setContent(content);
        contentIndexDto.setTagDtoList(tagDtoList);

        // 作者其他的文章
        bindAuthorOtherContent(contentIndexDto);
        return contentIndexDto;
    }


    /**
     * 处理文章收藏数
     *
     * @param content
     */
    private void handleContentCollectNum(Content content) {

        if (Objects.isNull(content)) {
            throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
        }
        Integer collectNum = collectService.collectNum(CcEnum.CollectType.CONTENT.getType(), content.getContentId());
        content.setCollectNum(collectNum);
    }

    private void bindAuthorOtherContent(ContentIndexDto contentIndexDto) {
        ListContentReq listContentReq = new ListContentReq();
        listContentReq.setPageNum(CcConstant.Page.NUM);
        listContentReq.setPageSize(CcConstant.Page.SIZE);
        listContentReq.setUserId(contentIndexDto.getContent().getUserId());
        listContentReq.setStatus(CcEnum.ContentStatus.NORMAL.getStatus());
        ListContentDto listContentDto = contentService.listContent(listContentReq);
        if (Objects.nonNull(listContentDto)) {
            List<Content> contentList = listContentDto.getContentList();
            if (!CollectionUtils.isEmpty(contentList)) {
                // 排除当前用户正在浏览的文章
                List<Content> otherContentList = contentList.stream().filter(c -> !c.getContentId().equals(contentIndexDto.getContent().getContentId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(otherContentList)) {
                    contentIndexDto.setAuthorOtherContentList(otherContentList);
                }
            }
        }
    }


}
