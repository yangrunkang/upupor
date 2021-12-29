package com.upupor.web.page;

import com.alibaba.fastjson.JSON;
import com.upupor.framework.utils.FileUtils;
import com.upupor.service.business.ad.AbstractAd;
import com.upupor.service.business.aggregation.CommonAggregateService;
import com.upupor.service.business.aggregation.ContentAggregateService;
import com.upupor.service.business.aggregation.service.*;
import com.upupor.service.common.*;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.File;
import com.upupor.service.dto.page.ContentIndexDto;
import com.upupor.service.utils.*;
import com.upupor.spi.req.GetContentReq;
import com.upupor.spi.req.GetMemberIntegralReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.ArrayUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.Page.SIZE_COMMENT;
import static com.upupor.service.common.CcConstant.SeoKey;
import static com.upupor.service.common.ErrorCode.ARTICLE_NOT_BELONG_TO_YOU;


/**
 * 内容页面跳转控制器
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/18 00:29
 */
@Slf4j
@Api(tags = "内容页面跳转,文件上传")
@RestController
@RequiredArgsConstructor
public class ContentPageJumpController {

    private final ContentAggregateService contentAggregateService;
    private final CollectService collectService;
    private final ContentService contentService;
    private final MessageService messageService;
    private final MemberIntegralService memberIntegralService;
    private final FileService fileService;
    private final CommentService commentService;
    private final ViewerService viewerService;
    private final RadioService radioService;
    @Value("${upupor.oss.file-host}")
    private String ossFileHost;
    @Value("${upupor.thumbnails.allows}")
    private String allowsFilesSuffix;

    /**
     * 内容浏览页面
     * <p>
     * http://localhost:8888/js/content/content.js 如果不加view 因为页面有个这样引入的js,会报 Uncaught SyntaxError: Unexpected token '<' 的错误
     *
     * @param contentId 内容Id
     * @return
     */
    @GetMapping(value = {"/u/{contentId}"})
    public ModelAndView content(@PathVariable("contentId") String contentId, Integer pageNum, Integer pageSize, String msgId) {
        if (Objects.isNull(pageNum)) {
            // 获取最新的评论
            Integer count = commentService.countByTargetId(contentId);
            pageNum = PageUtils.calcMaxPage(count, SIZE_COMMENT);
        }

        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE_COMMENT;
        }

        if (Objects.nonNull(msgId)) {
            messageService.tagMsgRead(msgId);
        }

        // 文章详情首页
        ContentIndexDto contentIndexDto = detailContentIndexDto(contentId, pageNum, pageSize);
        Content content = contentIndexDto.getContent();
        // 进一步校验访问来源
        if (!content.getStatus().equals(CcEnum.ContentStatus.NORMAL.getStatus())) {
            throw new BusinessException(ErrorCode.ILLEGAL_PATH_TO_VIEW_CONTENT);
        }

        // 文章浏览数数据+1
        contentService.viewNumPlusOne(content.getContentId());

        // 推荐文章
        contentIndexDto.setRandomContentList(contentService.randomContent(content.getUserId()));
        AbstractAd.ad(contentIndexDto.getRandomContentList());

        // 绑定文章其他数据
        bindContentOtherData(contentIndexDto, content);

        // 记录访问者
        viewerService.addViewer(contentId, CcEnum.ViewTargetType.CONTENT);

        // 文章作者是否有电台
        contentIndexDto.setHasRadio(radioService.userHasRadio(content.getUserId()));

        // 创建文章
        contentIndexDto.setCreateContentDesc(CommonAggregateService.getCreateContentInfo(content.getContentType(), null));

        // 返回到内容详情页
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(CcConstant.CONTENT_INDEX);
        modelAndView.addObject(contentIndexDto);
        modelAndView.addObject(SeoKey.TITLE, content.getTitle());
        modelAndView.addObject(SeoKey.DESCRIPTION, content.getTitle());
        // 文章详情设置关键字
        if (!StringUtils.isEmpty(content.getKeywords())) {
            modelAndView.addObject(SeoKey.KEYWORDS, content.getKeywords());
        } else {
            modelAndView.addObject(SeoKey.KEYWORDS, CcUtils.getSegmentResult(content.getTitle()));
        }

        return modelAndView;
    }

    /**
     * 个人中心访问文章详情-不增加浏览量
     *
     * @param contentId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = {"/m/{contentId}"})
    public ModelAndView mcontent(@PathVariable("contentId") String contentId, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        // 文章详情首页
        ContentIndexDto contentIndexDto = detailManageContentIndexDto(contentId, pageNum, pageSize);
        Content content = contentIndexDto.getContent();

        // 校验文章所属人
        String userId = ServletUtils.getUserId();
        if (!content.getUserId().equals(userId)) {
            throw new BusinessException(ARTICLE_NOT_BELONG_TO_YOU);
        }

        // 绑定文章其他数据
        bindContentOtherData(contentIndexDto, content);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(CcConstant.CONTENT_INDEX);
        modelAndView.addObject(contentIndexDto);
        modelAndView.addObject(SeoKey.TITLE, content.getTitle());
        modelAndView.addObject(SeoKey.DESCRIPTION, content.getTitle());
        return modelAndView;
    }

    /**
     * 返回方法参数
     * <p>
     * 要记住 @RequestParam("value")里面的value值,要和页面提交的一直,如果是页面是 filedddd,@RequestParam()里面就要写filedddd
     *
     * @param file
     * @return
     */
    @ApiOperation("编辑器上传图片文件")
    @PostMapping(value = "/uploadFile", consumes = "multipart/form-data")
    public String uploadFileForEditor(@RequestParam("upload") MultipartFile file) throws IOException {

        if (Objects.isNull(file)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件为空");
        }

        String fileType = FileUtils.getFileType(file.getInputStream());
        if (!fileType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "禁止上传非图像文件");
        }


        // 检查文件之前是否已经上传过
        String md5 = UpuporFileUtils.getMd5(file.getInputStream());
        File fileByMd5 = fileService.selectByMd5(md5);

        String pictureUrl;

        if (Objects.isNull(fileByMd5)) {
            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;
            String suffix = originalFilename.substring(originalFilename.lastIndexOf(CcConstant.ONE_DOTS) + 1);

            // 判定是否是允许上传文件后缀
            if (StringUtils.isEmpty(allowsFilesSuffix)) {
                throw new BusinessException(ErrorCode.LESS_CONFIG);
            }
            String[] split = allowsFilesSuffix.split(CcConstant.COMMA);
            if (ArrayUtils.isEmpty(split)) {
                throw new BusinessException(ErrorCode.LESS_CONFIG);
            }
            if (!Arrays.asList(split).contains(suffix)) {
                throw new BusinessException(ErrorCode.ILLEGAL_FILE_SUFFIX);
            }
            // 文件名
            String fileName = "content_" + CcUtils.getUuId() + CcConstant.ONE_DOTS + suffix;
            String folderFileName;
            try {
                folderFileName = "content/" + fileName;
                OssUtils.uploadImgFile(file, folderFileName, null);

                pictureUrl = ossFileHost + folderFileName;

                // 文件入库
                try {
                    File upuporFile = UpuporFileUtils.getUpuporFile(md5, pictureUrl, ServletUtils.getUserId());
                    fileService.addFile(upuporFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();

                log.error("文件上传失败,文件类型:{}", fileType);

                // 根据API文档封装错误消息
                CkeditorUploadErrorResponse errorResponse = new CkeditorUploadErrorResponse();
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setMessage(e.getMessage());
                errorResponse.setError(errorMessage);
                return JSON.toJSONString(errorResponse);
            }
        } else {
            pictureUrl = fileByMd5.getFileUrl();
        }


        // 根据API文档封装成功消息
        CkeditorUploadResponse res = new CkeditorUploadResponse();
        res.setUrl(pictureUrl);
        return JSON.toJSONString(res);
    }

    private ContentIndexDto detailContentIndexDto(String contentId, Integer pageNum, Integer pageSize) {

        if (StringUtils.isEmpty(contentId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        ContentIndexDto contentIndexDto;
        GetContentReq getContentReq = new GetContentReq();
        getContentReq.setContentId(contentId);
        try {
            contentIndexDto = contentAggregateService.contentDetail(contentId, pageNum, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            // 异常处理
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
            }
        }
        return contentIndexDto;
    }

    private ContentIndexDto detailManageContentIndexDto(String contentId, Integer pageNum, Integer pageSize) {

        if (StringUtils.isEmpty(contentId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        ContentIndexDto contentIndexDto;
        GetContentReq getContentReq = new GetContentReq();
        getContentReq.setContentId(contentId);
        try {
            contentIndexDto = contentAggregateService.contentManageDetail(contentId, pageNum, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            // 异常处理
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
            }
        }
        return contentIndexDto;
    }

    /**
     * 绑定文章常用数据
     *
     * @param contentIndexDto
     * @param content
     */
    private void bindContentOtherData(ContentIndexDto contentIndexDto, Content content) {
        // 设置是否收藏过
        settingIsCollect(contentIndexDto, content);

        // 设置文章是否点赞
        settingIsLike(contentIndexDto, content);

        // 设置当前用户是否关注作者
        settingIsAttention(contentIndexDto, content);

    }

    private void settingIsAttention(ContentIndexDto contentIndexDto, Content content) {
        String contentUserId = content.getUserId();
        contentIndexDto.setCurrUserIsAttention(contentService.currentUserIsAttentionAuthor(contentUserId));
    }

    private void settingIsLike(ContentIndexDto contentIndexDto, Content content) {
        boolean currUserIsClickLike = false;
        try {
            GetMemberIntegralReq getMemberIntegralReq = new GetMemberIntegralReq();
            getMemberIntegralReq.setUserId(ServletUtils.getUserId());
            getMemberIntegralReq.setRuleId(IntegralEnum.CLICK_LIKE.getRuleId());
            getMemberIntegralReq.setTargetId(content.getContentId());
            currUserIsClickLike = memberIntegralService.checkExists(getMemberIntegralReq);
        } catch (Exception ignored) {
        }
        contentIndexDto.setCurrUserIsClickLike(currUserIsClickLike);
    }

    private void settingIsCollect(ContentIndexDto contentIndexDto, Content content) {
        boolean currUserIsCollect = false;
        try {
            currUserIsCollect = collectService.existsCollectContent(content.getContentId(), ServletUtils.getUserId());
        } catch (Exception ignored) {
        }
        contentIndexDto.setCurrUserIsCollect(currUserIsCollect);
    }

    @Data
    private static class CkeditorUploadResponse {
        private String url;
    }

    @Data
    private static class CkeditorUploadErrorResponse {
        private ErrorMessage error;
    }

    @Data
    private static class ErrorMessage {
        private String message;
    }

}
