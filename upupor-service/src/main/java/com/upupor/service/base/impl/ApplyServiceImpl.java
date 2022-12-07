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

package com.upupor.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.data.dao.entity.Apply;
import com.upupor.data.dao.entity.ApplyDocument;
import com.upupor.data.dao.entity.converter.Converter;
import com.upupor.data.dao.entity.enhance.ApplyEnhance;
import com.upupor.data.dao.mapper.ApplyDocumentMapper;
import com.upupor.data.dao.mapper.ApplyMapper;
import com.upupor.data.types.ApplyStatus;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.CcUtils;
import com.upupor.framework.utils.FileUtils;
import com.upupor.service.utils.JwtUtils;
import com.upupor.service.base.ApplyService;
import com.upupor.service.base.FileService;
import com.upupor.service.base.MessageService;
import com.upupor.service.business.message.MessageSend;
import com.upupor.service.business.message.model.MessageModel;
import com.upupor.service.outer.req.AddApplyDocumentReq;
import com.upupor.service.outer.req.DelApplyReq;
import com.upupor.service.outer.req.UpdateApplyReq;
import com.upupor.service.utils.oss.FileUpload;
import com.upupor.service.utils.oss.enums.FileDic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import static com.upupor.framework.CcConstant.NOTIFY_ADMIN;

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
    public ApplyEnhance getByApplyId(String applyId) {
        LambdaQueryWrapper<Apply> query = new LambdaQueryWrapper<Apply>().eq(Apply::getApplyId, applyId);
        Apply apply = applyMapper.selectOne(query);
        if (Objects.isNull(apply)) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_APPLY);
        }
        return Converter.applyEnhance(apply);
    }

    @Override
    public Boolean editApply(UpdateApplyReq updateApplyReq) {
        String reqUserId = updateApplyReq.getUserId();
        JwtUtils.checkOperatePermission(reqUserId);

        String applyId = updateApplyReq.getApplyId();
        ApplyEnhance applyEnhance = getByApplyId(applyId);
        Apply apply = applyEnhance.getApply();
        apply.setApplyStatus(updateApplyReq.getStatus());
        return applyMapper.updateById(apply) > 0;
    }

    @Override
    public Integer commitDocument(AddApplyDocumentReq addApplyDocumentReq) throws IOException {


        String fileType = FileUtils.getFileType(addApplyDocumentReq.getFile().getInputStream());
        if ("application/x-sh".equals(fileType)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "禁止上传脚本文件");
        }

        String applyId = addApplyDocumentReq.getApplyId();
        ApplyEnhance applyEnhance = getByApplyId(applyId);
        Apply apply = applyEnhance.getApply();
        ApplyDocument applyDocument = new ApplyDocument();
        applyDocument.setApplyDocumentId(CcUtils.getUuId());
        applyDocument.setApplyId(applyId);
        applyDocument.setCopyWriting(addApplyDocumentReq.getApplyAdText().trim());
        if (Objects.nonNull(addApplyDocumentReq.getFile())) {
            applyDocument.setUpload(FileUpload.upload(addApplyDocumentReq.getFile(), FileDic.APPLY));
        }
        applyDocument.setCreateTime(CcDateUtil.getCurrentTime());
        applyDocument.setSysUpdateTime(new Date());

        int result = applyDocumentMapper.insert(applyDocument);
        if (result > 0) {
            // 提交材料成功,更改状态
            apply.setApplyStatus(ApplyStatus.APPLY_DOCUMENT_COMMIT);
            applyMapper.updateById(apply);

            // 发送邮件
            String emailContent = "收到新的申请,请尽快处理";

            MessageSend.send(MessageModel.builder()
                    .toUserId(NOTIFY_ADMIN)
                    .emailModel(MessageModel.EmailModel.builder()
                            .title("广告申请材料提交!!!请尽快处理")
                            .content("广告申请材料:" + emailContent)
                            .build())
                    .messageId(CcUtils.getUuId())
                    .build());

        }

        return result;
    }


    @Override
    public Boolean delApply(DelApplyReq delApplyReq) {
        String reqUserId = delApplyReq.getUserId();
        String applyId = delApplyReq.getApplyId();

        JwtUtils.checkOperatePermission(reqUserId);

        ApplyEnhance applyEnhance = getByApplyId(applyId);
        Apply apply = applyEnhance.getApply();
        if (Objects.isNull(apply)) {
            throw new BusinessException(ErrorCode.NOT_EXISTS_APPLY);
        }
        apply.setApplyStatus(ApplyStatus.APPLY_DELETED);

        return applyMapper.updateById(apply) > 0;
    }
}
