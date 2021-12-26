package com.upupor.service.service.aggregation;

import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dao.entity.Tag;
import com.upupor.service.dto.page.MemberIndexDto;
import com.upupor.service.dto.page.common.ListCommentDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.service.dto.page.common.ListRadioDto;
import com.upupor.service.service.*;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.ListCommentReq;
import com.upupor.spi.req.ListContentReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 个人主页聚合服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/26 19:41
 */
@Service
@RequiredArgsConstructor
public class ProfileAggregateService {

    private final MemberService memberService;
    private final ContentService contentService;
    private final TagService tagService;
    private final FanService fanService;
    private final CommentService commentService;
    private final AttentionService attentionService;
    private final ViewerService viewerService;
    private final RadioService radioService;

    public MemberIndexDto index(String userId, Integer pageNum, Integer pageSize, CcEnum.ViewTargetType source) {
        MemberIndexDto memberIndexDto = new MemberIndexDto();

        Member member = memberService.memberInfo(userId);
        if (Objects.isNull(member)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXISTS);
        }

        // 设置用户粉丝数
        int fansNum = fanService.getFansNum(member.getUserId());
        member.setFansNum(fansNum);

        // 设置用户关注数
        Integer attentionNum = attentionService.getAttentionNum(member.getUserId());
        member.setAttentionNum(attentionNum);

        // 设置文章总数
        Integer totalContentNum = contentService.getUserTotalContentNum(member.getUserId());
        member.setTotalContentNum(totalContentNum);

        ListContentDto listContentDto = new ListContentDto();
        ListRadioDto listRadioDto = new ListRadioDto();
        ListCommentDto listCommentDto = new ListCommentDto();


        if (CcEnum.ViewTargetType.PROFILE_CONTENT.equals(source)) {
            ListContentReq listContentReq = new ListContentReq();
            listContentReq.setUserId(userId);
            listContentReq.setStatus(CcEnum.ContentStatus.NORMAL.getStatus());
            listContentReq.setPageNum(pageNum);
            listContentReq.setPageSize(pageSize);
            listContentDto = contentService.listContent(listContentReq);
            // 绑定文章数据
            contentService.bindContentData(listContentDto);
            // 记录访问者
            viewerService.addViewer(userId, CcEnum.ViewTargetType.PROFILE_CONTENT);
            //获取访问者
            memberIndexDto.setViewerList(viewerService.listViewerByTargetIdAndType(userId, CcEnum.ViewTargetType.PROFILE_CONTENT));
        }

        if (CcEnum.ViewTargetType.PROFILE_RADIO.equals(source)) {
            listRadioDto = radioService.listRadioByUserId(pageNum, pageSize, userId, null);
            // 记录访问者
            viewerService.addViewer(userId, CcEnum.ViewTargetType.PROFILE_RADIO);
            //获取访问者
            memberIndexDto.setViewerList(viewerService.listViewerByTargetIdAndType(userId, CcEnum.ViewTargetType.PROFILE_RADIO));
        }

        if (CcEnum.ViewTargetType.PROFILE_MESSAGE.equals(source)) {
            listCommentDto = message(userId, pageNum, pageSize);
            // 记录访问者
            viewerService.addViewer(userId, CcEnum.ViewTargetType.PROFILE_MESSAGE);
            // 设置访问者
            memberIndexDto.setViewerList(viewerService.listViewerByTargetIdAndType(userId, CcEnum.ViewTargetType.PROFILE_MESSAGE));
        }


        // 获取用户总积分值
        Integer totalIntegral = memberService.totalIntegral(member.getUserId());
        member.setTotalIntegral(totalIntegral);

        memberIndexDto.setTagList(getUserTagList(userId));
        memberIndexDto.setCurrUserIsAttention(contentService.currentUserIsAttentionAuthor(userId));

        // 设置用户声明
        memberService.bindStatement(member);

        try {
            if (member.getUserId().equals(ServletUtils.getUserId())) {
                memberIndexDto.setIsThatYouEnterYourProfile(true);
            }
        } catch (Exception e) {

        }

        // 个人主页文章列表添加广告
        if (!CollectionUtils.isEmpty(listContentDto.getContentList())) {
            boolean exists = listContentDto.getContentList().parallelStream().anyMatch(t -> t.getContentId().equals(CcConstant.GoogleAd.FEED_AD));
            if (!exists) {
                int adIndex = new Random().nextInt(CcConstant.Page.SIZE);
                int maxIndex = listContentDto.getContentList().size() - 1;
                if (adIndex <= 2) {
                    adIndex = 3;
                }
                if (adIndex >= maxIndex) {
                    adIndex = maxIndex;
                }
                Content adContent = new Content();
                adContent.setContentId(CcConstant.GoogleAd.FEED_AD);
                adContent.setUserId(CcConstant.GoogleAd.FEED_AD);
                listContentDto.getContentList().add(adIndex, adContent);
            }
        }

        // 个人主页电台列表添加广告
        if (!CollectionUtils.isEmpty(listRadioDto.getRadioList())) {
            boolean exists = listRadioDto.getRadioList().parallelStream().anyMatch(t -> t.getRadioId().equals(CcConstant.GoogleAd.FEED_AD));
            if (!exists) {
                int adIndex = new Random().nextInt(CcConstant.Page.SIZE);
                int maxIndex = listRadioDto.getRadioList().size() - 1;
                if (adIndex <= 2) {
                    adIndex = 3;
                }
                if (adIndex >= maxIndex) {
                    adIndex = maxIndex;
                }
                Radio radio = new Radio();
                radio.setRadioId(CcConstant.GoogleAd.FEED_AD);
                radio.setUserId(CcConstant.GoogleAd.FEED_AD);
                listRadioDto.getRadioList().add(adIndex, radio);
            }
        }

        // 用户文章
        memberIndexDto.setListContentDto(listContentDto);
        // 用户电台
        memberIndexDto.setListRadioDto(listRadioDto);
        // 用户留言
        memberIndexDto.setListCommentDto(listCommentDto);

        // 获取用户属性
        memberIndexDto.setMember(member);
        return memberIndexDto;
    }

    private List<Tag> getUserTagList(String userId) {

        // 获取用户标签
        List<Content> contentList = contentService.listAllByUserId(userId);

        if (CollectionUtils.isEmpty(contentList)) {
            return new ArrayList<>();
        }

        List<String> tagIdList = contentList.stream()
                .map(Content::getTagIds).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tagIdList)) {
            return new ArrayList<>();
        }
        String s = CcUtils.convertListToStr(tagIdList);
        String[] split = s.split(CcConstant.COMMA);
        if (ArrayUtils.isEmpty(split)) {
            return new ArrayList<>();
        }

        List<Tag> tags = tagService.listByTagIdList(Arrays.asList(split));
        if (CollectionUtils.isEmpty(tags)) {
            return new ArrayList<>();
        }
        return tags;
    }


    public ListCommentDto message(String userId, Integer pageNum, Integer pageSize) {

        MemberIndexDto memberIndexDto = new MemberIndexDto();

        ListCommentReq listCommentReq = new ListCommentReq();
        listCommentReq.setPageNum(pageNum);
        listCommentReq.setPageSize(pageSize);
        listCommentReq.setTargetId(userId);
        listCommentReq.setStatus(CcEnum.CommentStatus.NORMAL.getStatus());
        listCommentReq.setCommentSource(CcEnum.CommentSource.MESSAGE.getSource());

        ListCommentDto listCommentDto = commentService.listComment(listCommentReq);
        if (Objects.isNull(listCommentDto)) {
            listCommentDto = new ListCommentDto();
        }

        // 绑定评论用户
        if (!CollectionUtils.isEmpty(listCommentDto.getCommentList())) {
            commentService.bindCommentUserName(listCommentDto.getCommentList());
        }

        memberIndexDto.setListCommentDto(listCommentDto);
        return listCommentDto;
    }
}
