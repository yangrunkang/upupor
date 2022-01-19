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

package com.upupor.service.business.aggregation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.FileUtils;
import com.upupor.service.business.aggregation.dao.entity.Apply;
import com.upupor.service.business.aggregation.dao.entity.ApplyDocument;
import com.upupor.service.business.aggregation.dao.entity.File;
import com.upupor.service.business.aggregation.dao.mapper.ApplyDocumentMapper;
import com.upupor.service.business.aggregation.dao.mapper.ApplyMapper;
import com.upupor.service.business.aggregation.service.ApplyService;
import com.upupor.service.business.aggregation.service.FileService;
import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.common.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.service.common.ErrorCode;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.service.outer.req.AddApplyDocumentReq;
import com.upupor.service.outer.req.DelApplyReq;
import com.upupor.service.outer.req.UpdateApplyReq;
import com.upupor.service.types.ApplyStatus;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.OssUtils;
import com.upupor.service.utils.ServletUtils;
import com.upupor.service.utils.UpuporFileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import static com.upupor.framework.CcConstant.SKIP_SUBSCRIBE_EMAIL_CHECK;

/**
 * 申请服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/24 11:35
 */
@Service
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {

    private final ApplyMapper applyMapper;
    private final FileService fileService;
    private final ApplyDocumentMapper applyDocumentMapper;
    private final MessageService messageService;
    private final UpuporConfig upuporConfig;


    @Override
    public Integer addApply(Apply apply) {
        return applyMapper.insert(apply);
    }

    @Override
    public Apply getByApplyId(String applyId) {
        LambdaQueryWrapper<Apply> query = new LambdaQueryWrapper<Apply>()
                .eq(Apply::getApplyId, applyId);
        return applyMapper.selectOne(query);
    }

    @Override
    public Boolean editApply(UpdateApplyReq updateApplyReq) {
        String reqUserId = updateApplyReq.getUserId();
        ServletUtils.checkOperatePermission(reqUserId);

        String applyId = updateApplyReq.getApplyId();
        Apply apply = getByApplyId(applyId);
        if (Objects.isNull(apply)) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_APPLY);
        }
        apply.setApplyStatus(updateApplyReq.getStatus());


        return applyMapper.updateById(apply) > 0;
    }

    @Override
    public Integer commitDocument(AddApplyDocumentReq addApplyDocumentReq) throws IOException {
        String userId = ServletUtils.getUserId();
        ServletUtils.checkOperatePermission(userId);

        String fileType = FileUtils.getFileType(addApplyDocumentReq.getFile().getInputStream());
        if ("application/x-sh".equals(fileType)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "禁止上传脚本文件");
        }

        String applyId = addApplyDocumentReq.getApplyId();
        Apply apply = getByApplyId(applyId);
        if (Objects.isNull(apply)) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_APPLY);
        }

        ApplyDocument applyDocument = new ApplyDocument();
        applyDocument.setApplyDocumentId(CcUtils.getUuId());
        applyDocument.setApplyId(applyId);
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
                fileUrl = upuporConfig.getOss().getFileHost() + folderName;
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

        Integer result = applyDocumentMapper.insert(applyDocument);

        if (result > 0) {
            // 提交材料成功,更改状态
            apply.setApplyStatus(ApplyStatus.APPLY_DOCUMENT_COMMIT);
            applyMapper.updateById(apply);

            //
            // 发送邮件
            String emailContent = "收到新的申请,请尽快处理";
            messageService.sendEmail(CcConstant.UPUPOR_EMAIL, "广告申请材料提交!!!请尽快处理", "广告申请材料:" + emailContent, SKIP_SUBSCRIBE_EMAIL_CHECK);
        }

        return result;
    }

    @Override
    public Boolean delApply(DelApplyReq delApplyReq) {
        String reqUserId = delApplyReq.getUserId();
        String applyId = delApplyReq.getApplyId();

        ServletUtils.checkOperatePermission(reqUserId);

        Apply apply = getByApplyId(applyId);
        if (Objects.isNull(apply)) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_APPLY);
        }
        apply.setApplyStatus(ApplyStatus.APPLY_DELETED);

        return applyMapper.updateById(apply) > 0;
    }
}
