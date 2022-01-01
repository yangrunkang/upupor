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

package com.upupor.web.page;

import com.alibaba.fastjson.JSON;
import com.upupor.framework.utils.FileUtils;
import com.upupor.service.business.aggregation.service.CommentService;
import com.upupor.service.business.aggregation.service.FileService;
import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.business.content.ContentAggregateService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.File;
import com.upupor.service.dto.page.ContentIndexDto;
import com.upupor.service.utils.*;
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
    private final MessageService messageService;
    private final FileService fileService;
    private final CommentService commentService;
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
        ContentIndexDto contentIndexDto = contentAggregateService.contentDetail(contentId, pageNum, pageSize);

        // 返回到内容详情页
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(CcConstant.CONTENT_INDEX);
        modelAndView.addObject(contentIndexDto);
        modelAndView.addObject(SeoKey.TITLE, contentIndexDto.getContent().getTitle());
        modelAndView.addObject(SeoKey.DESCRIPTION, contentIndexDto.getContent().getTitle());
        // 文章详情设置关键字
        if (!StringUtils.isEmpty(contentIndexDto.getContent().getKeywords())) {
            modelAndView.addObject(SeoKey.KEYWORDS, contentIndexDto.getContent().getKeywords());
        } else {
            modelAndView.addObject(SeoKey.KEYWORDS, CcUtils.getSegmentResult(contentIndexDto.getContent().getTitle()));
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
        ContentIndexDto contentIndexDto = contentAggregateService.contentManageDetail(contentId, pageNum, pageSize);
        Content content = contentIndexDto.getContent();


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
