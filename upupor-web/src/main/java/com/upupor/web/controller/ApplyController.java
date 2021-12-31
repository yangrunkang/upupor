/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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

package com.upupor.web.controller;

import com.alibaba.fastjson.JSON;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.FileUtils;
import com.upupor.service.business.aggregation.service.ApplyService;
import com.upupor.service.business.aggregation.service.FileService;
import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcResponse;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Apply;
import com.upupor.service.dao.entity.ApplyDocument;
import com.upupor.service.dao.entity.File;
import com.upupor.service.dto.page.apply.ApplyContentDto;
import com.upupor.service.spi.req.*;
import com.upupor.service.types.ApplySource;
import com.upupor.service.types.ApplyStatus;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.OssUtils;
import com.upupor.service.utils.ServletUtils;
import com.upupor.service.utils.UpuporFileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.SKIP_SUBSCRIBE_EMAIL_CHECK;


/**
 * 申请控制层
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/24 11:19
 */
@Api(tags = "申请服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/apply")
public class ApplyController {
    private final ApplyService applyService;
    private final FileService fileService;
    private final MessageService messageService;

    @Value("${upupor.oss.file-host}")
    private String ossFileHost;

    @ApiOperation("添加广告申请")
    @PostMapping(value = "/addAd")
    @ResponseBody
    public CcResponse addAd(AddApplyReq adApplyReq) {

        CcResponse ccResponse = new CcResponse();
        if (CcUtils.isAllEmpty(adApplyReq.getApplyUserEmail(), adApplyReq.getApplyUserPhone(),
                adApplyReq.getApplyUserQq(), adApplyReq.getApplyUserWechat())) {
            throw new BusinessException(ErrorCode.AD_APPLY_LEAST_CONTACT);
        }
        Apply apply = new Apply();
        BeanUtils.copyProperties(adApplyReq, apply);
        apply.setApplyId(CcUtils.getUuId());
        apply.setUserId(ServletUtils.getUserId());

        ApplyContentDto applyContentDto = new ApplyContentDto();
        applyContentDto.setApplyIntro(adApplyReq.getAdIntro());
        applyContentDto.setApplyProject(CcUtils.removeLastComma(adApplyReq.getPositionIdList()));

        apply.setApplySource(adApplyReq.getType());
        apply.setApplyContent(JSON.toJSONString(applyContentDto));
        apply.setApplyStatus(ApplyStatus.WAIT_APPLY);
        apply.setCreateTime(CcDateUtil.getCurrentTime());
        apply.setSysUpdateTime(new Date());

        ccResponse.setData(applyService.addApply(apply) > 0);

        // 发送邮件
        String emailContent = "申请id:%s" + "\n" +
                "用户id:%s" + "\n" +
                "申请简介:%s" + "\n" +
                "申请项目:%s" + "\n";
        emailContent = String.format(emailContent, apply.getApplyId(),
                apply.getUserId(),
                applyContentDto.getApplyIntro(),
                applyContentDto.getApplyProject()
        );
        messageService.sendEmail(CcConstant.UPUPOR_EMAIL, "您有新的广告申请!!!请尽快处理", "广告申请:" + emailContent, SKIP_SUBSCRIBE_EMAIL_CHECK);


        return ccResponse;
    }

    @ApiOperation("添加咨询服务申请")
    @PostMapping(value = "/addConsultant")
    @ResponseBody
    public CcResponse addConsultant(AddConsultantReq addConsultantReq) {

        CcResponse ccResponse = new CcResponse();
        if (CcUtils.isAllEmpty(addConsultantReq.getTopic(), addConsultantReq.getDesc())) {
            throw new BusinessException(ErrorCode.PATH_ERROR);
        }

        Apply apply = new Apply();
        apply.setApplyId(CcUtils.getUuId());
        apply.setUserId(ServletUtils.getUserId());

        ApplyContentDto applyContentDto = new ApplyContentDto();
        applyContentDto.setApplyProject(addConsultantReq.getTopic());
        applyContentDto.setApplyIntro(addConsultantReq.getDesc());

        apply.setApplySource(addConsultantReq.getType());
        apply.setApplyContent(JSON.toJSONString(applyContentDto));
        apply.setApplyStatus(ApplyStatus.WAIT_APPLY);
        apply.setCreateTime(CcDateUtil.getCurrentTime());
        apply.setSysUpdateTime(new Date());

        ccResponse.setData(applyService.addApply(apply) > 0);

        // 发送邮件
        String emailContent = "申请id:%s" + "\n" +
                "用户id:%s" + "\n" +
                "申请简介:%s" + "\n" +
                "申请项目:%s" + "\n";
        emailContent = String.format(emailContent, apply.getApplyId(),
                apply.getUserId(),
                applyContentDto.getApplyIntro(),
                applyContentDto.getApplyProject()
        );
        messageService.sendEmail(CcConstant.UPUPOR_EMAIL, "您有新的咨询服务!!!请尽快处理", "咨询服务内容:" + emailContent, SKIP_SUBSCRIBE_EMAIL_CHECK);


        return ccResponse;
    }

    @ApiOperation("添加标签申请")
    @PostMapping(value = "/addTag")
    @ResponseBody
    public CcResponse addTag(AddTagReq addTagReq) {
        CcResponse ccResponse = new CcResponse();

        Apply apply = new Apply();
        apply.setApplyId(CcUtils.getUuId());
        apply.setUserId(ServletUtils.getUserId());
        apply.setApplySource(ApplySource.TAG);
        apply.setApplyContent(JSON.toJSONString(addTagReq));
        apply.setApplyStatus(ApplyStatus.WAIT_APPLY);
        apply.setCreateTime(CcDateUtil.getCurrentTime());
        apply.setSysUpdateTime(new Date());

        ccResponse.setData(applyService.addApply(apply) > 0);


        String emailContent = "申请id:%s" + "\n" +
                "用户id:%s" + "\n" +
                "申请页面:%s" + "\n" +
                "申请标签:%s" + "\n" +
                "申请子标签:%s";
        emailContent = String.format(emailContent, apply.getApplyId(),
                apply.getUserId(),
                addTagReq.getPageName(),
                addTagReq.getTagName(),
                addTagReq.getChildTagName()
        );
        messageService.sendEmail(CcConstant.UPUPOR_EMAIL, "您有新的标签添加申请!!!请尽快处理", "标签添加内容:" + emailContent, SKIP_SUBSCRIBE_EMAIL_CHECK);

        return ccResponse;
    }

    @ApiOperation("删除申请")
    @PostMapping(value = "/del")
    @ResponseBody
    public CcResponse del(DelApplyReq delApplyReq) {
        CcResponse ccResponse = new CcResponse();

        String reqUserId = delApplyReq.getUserId();
        String applyId = delApplyReq.getApplyId();

        ServletUtils.checkOperatePermission(reqUserId);

        Apply apply = applyService.getByApplyId(applyId);
        if (Objects.isNull(apply)) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_APPLY);
        }
        apply.setApplyStatus(ApplyStatus.APPLY_DELETED);

        Integer result = applyService.update(apply);
        ccResponse.setData(result > 0);
        return ccResponse;
    }

    @ApiOperation("编辑申请")
    @PostMapping(value = "/edit")
    @ResponseBody
    public CcResponse edit(UpdateApplyReq updateApplyReq) {
        CcResponse ccResponse = new CcResponse();

        String reqUserId = updateApplyReq.getUserId();
        ServletUtils.checkOperatePermission(reqUserId);

        String applyId = updateApplyReq.getApplyId();
        Apply apply = applyService.getByApplyId(applyId);
        if (Objects.isNull(apply)) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_APPLY);
        }
        apply.setApplyStatus(updateApplyReq.getStatus());

        Integer result = applyService.update(apply);
        ccResponse.setData(result > 0);
        return ccResponse;
    }

    @ApiOperation("提交申请材料")
    @PostMapping(value = "/commit")
    @ResponseBody
    public CcResponse edit(AddApplyDocumentReq addApplyDocumentReq) throws Exception {
        CcResponse ccResponse = new CcResponse();
        String userId = ServletUtils.getUserId();
        ServletUtils.checkOperatePermission(userId);

        String fileType = FileUtils.getFileType(addApplyDocumentReq.getFile().getInputStream());
        if ("application/x-sh".equals(fileType)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "禁止上传脚本文件");
        }

        String applyId = addApplyDocumentReq.getApplyId();
        Apply apply = applyService.getByApplyId(applyId);
        if (Objects.isNull(apply)) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_APPLY);
        }

        ApplyDocument applyDocument = new ApplyDocument();
        applyDocument.setApplyDocumentId(CcUtils.getUuId());

        applyDocument.setApplyId(applyId);
        // 用户输入的图片url
//        applyDocument.setImgUrl(addApplyDocumentReq.getAdImgUrl());
        applyDocument.setCopyWriting(addApplyDocumentReq.getApplyAdText().trim());

        if (Objects.nonNull(addApplyDocumentReq.getFile())) {
            // 检查文件之前是否已经上传过
            String md5 = UpuporFileUtils.getMd5(addApplyDocumentReq.getFile().getInputStream());
            File fileByMd5 = fileService.selectByMd5(md5);
            String fileUrl;
            if (Objects.isNull(fileByMd5)) {
                // 上传的文件
                String originalFilename = addApplyDocumentReq.getFile().getOriginalFilename();
                assert originalFilename != null;
                String suffix = originalFilename.substring(originalFilename.lastIndexOf(CcConstant.ONE_DOTS) + 1);
                String fileName = "apply_" + CcUtils.getUuId() + CcConstant.ONE_DOTS + suffix;
                String folderName = "apply/" + fileName;
                OssUtils.uploadAnyFile(addApplyDocumentReq.getFile(), folderName);
                fileUrl = ossFileHost + folderName;
                // 文件入库
                try {
                    File upuporFile = UpuporFileUtils.getUpuporFile(md5, fileUrl, userId);
                    fileService.addFile(upuporFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                fileUrl = fileByMd5.getFileUrl();
            }
            applyDocument.setUpload(fileUrl);
        }


        applyDocument.setCreateTime(CcDateUtil.getCurrentTime());
        applyDocument.setSysUpdateTime(new Date());

        Integer result = applyService.commitDocument(applyDocument);

        if (result > 0) {
            // 提交材料成功,更改状态
            apply.setApplyStatus(ApplyStatus.APPLY_DOCUMENT_COMMIT);
            applyService.update(apply);
        }

        // 发送邮件
        String emailContent = "收到新的申请,请尽快处理";
        messageService.sendEmail(CcConstant.UPUPOR_EMAIL, "广告申请材料提交!!!请尽快处理", "广告申请材料:" + emailContent, SKIP_SUBSCRIBE_EMAIL_CHECK);

        ccResponse.setData(result > 0);

        return ccResponse;
    }

}
