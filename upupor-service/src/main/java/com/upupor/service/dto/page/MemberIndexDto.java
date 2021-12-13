package com.upupor.service.dto.page;

import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.Statement;
import com.upupor.service.dao.entity.Tag;
import com.upupor.service.dao.entity.Viewer;
import com.upupor.service.dto.page.common.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户首页Dto-用户管理
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/19 23:54
 */
@Data
public class MemberIndexDto {

    /**
     * 用户信息
     */
    private Member member;

    /**
     * 内容列表对象
     */
    private ListContentDto listContentDto;

    /**
     * 审核对象
     */
    private ListApplyDto listApplyDto;

    /**
     * 单个审核对象Dto
     */
    private ApplyDto applyDto;

    /**
     * 评论对象
     */
    private ListCommentDto listCommentDto;

    /**
     * 收藏对象
     */
    private ListCollectDto listCollectDto;

    /**
     * 关注对象
     */
    private ListAttentionDto listAttentionDto;

    /**
     * 粉丝对象
     */
    private ListFansDto listFansDto;

    /**
     * 消息
     */
    private ListMessageDto listMessageDto;

    /**
     * 积分记录
     */
    private ListIntegralDto listIntegralDto;

    private ListCssPatternDto listCssPatternDto;

    private ListRadioDto listRadioDto;
    /**
     * 作者标签
     * (作者写过的文章中做包含的标签)
     */
    private List<Tag> tagList;
    /**
     * 作者是否已经被你关注
     */
    private Boolean currUserIsAttention;
    /**
     * 是否是你自己进入到自己的公共主页
     */
    private Boolean isThatYouEnterYourProfile;
    /**
     * 备注
     */
    private Statement statement;
    /**
     * 文章访问者
     */
    private List<Viewer> viewerList;

    public MemberIndexDto() {
        this.member = new Member();
        this.listContentDto = new ListContentDto();
        this.listApplyDto = new ListApplyDto();
        this.listCommentDto = new ListCommentDto();
        this.listCollectDto = new ListCollectDto();
        this.listAttentionDto = new ListAttentionDto();
        this.listFansDto = new ListFansDto();
        this.listMessageDto = new ListMessageDto();
        this.listIntegralDto = new ListIntegralDto();
        this.listRadioDto = new ListRadioDto();

        this.tagList = new ArrayList<>();
        this.viewerList = new ArrayList<>();
        this.currUserIsAttention = false;
        this.isThatYouEnterYourProfile = false;
    }

}
