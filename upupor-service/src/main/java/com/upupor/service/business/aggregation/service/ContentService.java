package com.upupor.service.business.aggregation.service;

import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.ContentData;
import com.upupor.service.dao.entity.ContentEditReason;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dto.page.ContentIndexDto;
import com.upupor.service.dto.page.common.CountTagDto;
import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.spi.req.AddContentDetailReq;
import com.upupor.spi.req.ListContentReq;
import com.upupor.spi.req.UpdateContentReq;

import java.util.List;

/**
 * 内容服务
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 07:58
 */
public interface ContentService {
    /**
     * 获取文章详情
     *
     * @param contentId
     * @return
     */
    Content getContentDetail(String contentId);

    /**
     * 获取文章
     *
     * @param contentId
     * @return
     */
    Content getNormalContent(String contentId);


    Content getContentByContentIdNoStatus(String contentId);

    /**
     * 获取文章列表
     *
     * @param listContentReq
     * @return
     */
    ListContentDto listContent(ListContentReq listContentReq);

    /**
     * 文章列表
     *
     * @param listContentReq
     * @return
     */
    ListContentDto listContents(ListContentReq listContentReq);

    /**
     * 根据标题搜索
     *
     * @param listContentReq
     * @return
     */
    ListContentDto listContentByTitleAndShortContent(ListContentReq listContentReq);


    /**
     * 根据文章类型来获取文章列表
     *
     * @param pageNum
     * @param pageSize
     * @param contentType
     * @return
     */
    ListContentDto listContentByContentType(Integer contentType, Integer pageNum, Integer pageSize, String tag);

    /**
     * com.upupor.service.service.aggregation.service.ContentService#listContentByContentType(...) 的重载
     *
     * @param contentType
     * @param pageNum
     * @param pageSize
     * @return
     */
    ListContentDto listContentByContentType(Integer contentType, Integer pageNum, Integer pageSize);

    /**
     * 添加内容
     *
     * @param addContentDetailReq
     * @return
     */
    Boolean addContent(AddContentDetailReq addContentDetailReq);


    /**
     * 更新内容
     *
     * @param updateContentReq
     * @return
     */
    Boolean updateContent(UpdateContentReq updateContentReq);

    /**
     * 更新内容
     *
     * @param content
     * @return
     */
    Boolean updateContent(Content content);

    /**
     * 根据内容Id集合获取内容
     *
     * @param contentIdList
     * @return
     */
    List<Content> listByContentIdList(List<String> contentIdList);

    /**
     * 根据用户id查询所有文章
     *
     * @author runkangyang (cruise)
     * @date 2020.01.26 19:59
     */
    List<Content> listAllByUserId(String userId);

    /**
     * 文章总数
     */
    Integer total();

    /**
     * 文章浏览总数
     *
     * @return
     */
    Integer viewTotal();

    /**
     * 绑定文章数据
     *
     * @param contentList
     */
    void bindContentData(List<Content> contentList);

    /**
     * 绑定电台数据
     *
     * @param radioList
     */
    void bindRadioContentData(List<Radio> radioList);


    /**
     * 绑定个人详情页内容数据
     *
     * @param listContentDto
     */
    void bindContentData(ListContentDto listContentDto);

    /**
     * 绑定文章扩展信息
     *
     * @param content
     */
    void bindContentExtend(Content content);

    /**
     * 绑定用户名
     *
     * @param listContentDto
     */
    void bindContentMember(ListContentDto listContentDto);

    /**
     * 绑定用户名
     *
     * @param content
     */
    void bindContentMember(Content content);

    /**
     * 绑定用户名
     *
     * @param contentList
     */
    void bindContentMember(List<Content> contentList);

    /**
     * 检查内容状态是否正常
     *
     * @param contentId
     * @return true: 正常   false: 非正常
     */
    Boolean checkStatusIsOk(String contentId);

    /**
     * 根据类型获取文章总数
     *
     * @param contentType
     * @return
     */
    Integer getTotalByContentType(Integer contentType);

    Boolean currentUserIsAttentionAuthor(String contentUserId);

    /**
     * 获取管理页的内容详情
     *
     * @param contentId
     * @return
     */
    Content getManageContentDetail(String contentId);

    /**
     * 获取文章
     *
     * @return
     */
    List<Content> list(Integer pageNum, Integer pageSize);

    /**
     * 初始化文章数据
     *
     * @param targetId
     */
    void initContendData(String targetId);

    /**
     * 添加内容浏览数
     *
     * @param targetId
     */
    void viewNumPlusOne(String targetId);

    /**
     * 根据置顶状态获取文章列表
     *
     * @param pinnedStatus
     * @return
     */
    List<Content> getContentListByPinned(Integer pinnedStatus, String userId);


    void handlePinnedContent(ListContentDto listContentDto, String userId);

    ContentEditReason latestEditReason(String contentId);



    void bindContentEditReason(Content content);

    void bindContentStatement(Content content);

    void bindAuthorOtherContent(ContentIndexDto contentIndexDto, Content content);

    void bindContentTagName(ContentIndexDto contentIndexDto, Content content);

    /**
     * 罗列所有的标签
     *
     * @return
     */
    List<CountTagDto> listAllTag();

    /**
     * 绑定点赞的人
     *
     * @param content
     */
    void bindLikesMember(Content content);

    /**
     * 用户最新的文章
     *
     * @param userId
     * @return
     */
    Content latestContent(String userId);

    /**
     * 上一篇、下一篇
     *
     * @return
     */
    void lastAndNextContent(Content content);


    Boolean updateContentStatus(UpdateContentReq updateContentReq);

    /**
     * 草稿总数
     *
     * @param userId
     * @return
     */
    Integer countDraft(String userId);

    /**
     * 随机文章
     *
     * @return
     */
    List<Content> randomContent(String notUserId);

    /**
     * 获取文章总数
     *
     * @param userId
     * @return
     */
    Integer getUserTotalContentNum(String userId);

    void updateContentData(ContentData contentData);
}
