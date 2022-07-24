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

package com.upupor.service.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.framework.ErrorCode;
import com.upupor.service.data.dao.entity.Draft;
import com.upupor.service.data.dao.mapper.DraftMapper;
import com.upupor.service.data.service.DraftService;
import com.upupor.service.dto.dao.ListDraftDto;
import com.upupor.service.outer.req.content.AutoSaveContentReq;
import com.upupor.service.utils.Asserts;
import com.upupor.service.utils.ServletUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-07-20 00:30
 * @email: yangrunkang53@gmail.com
 */
@Service
public class DraftServiceImpl implements DraftService {

    @Resource
    private DraftMapper draftMapper;

    @Override
    public Boolean create(Draft draft) {
        return draftMapper.insert(draft) > 0;
    }

    @Override
    public Boolean update(Draft draft) {
        Asserts.notNull(draft.getId(), ErrorCode.WITHOUT_OPERATION_ID);
        return draftMapper.updateById(draft) > 0;
    }

    @Override
    public Boolean delete(Long id) {
        Asserts.notNull(id, ErrorCode.WITHOUT_OPERATION_ID);
        return draftMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean autoSaveContent(AutoSaveContentReq autoSaveContentReq) {
        String draftId = autoSaveContentReq.getDraftId(); // 草稿Id
        String userId = ServletUtils.getUserId();
        Draft draft = this.getByDraftIdAndUserId(draftId, userId);

        Boolean autoSave;
        if (Objects.isNull(draft)) {
            // 新文章,入库
            Draft newDraft = Draft.create(autoSaveContentReq, userId);
            autoSave = this.create(newDraft);
        } else {
            if (autoSaveContentReq.getDraftContent().equals(draft.getDraftContent())) { // 内容json包含title和content,只需要比较这个值
                return Boolean.TRUE;
            }
            draft.setDraftContent(autoSaveContentReq.getDraftContent());
            draft.setTitle(Draft.parseContent(draft).getTitle());
            draft.setSysUpdateTime(new Date());
            autoSave = this.update(draft);
        }

        return autoSave;
    }

    @Override
    public List<Draft> listByDto(ListDraftDto listDraftDto) {
        LambdaQueryWrapper<Draft> draftQuery = new LambdaQueryWrapper<>();
        draftQuery.eq(Objects.nonNull(listDraftDto.getId()), Draft::getId, listDraftDto.getId());
        draftQuery.eq(Objects.nonNull(listDraftDto.getUserId()), Draft::getUserId, listDraftDto.getUserId());
        draftQuery.eq(Objects.nonNull(listDraftDto.getDraftId()), Draft::getDraftId, listDraftDto.getDraftId());
        draftQuery.like(Objects.nonNull(listDraftDto.getSearchTitle()), Draft::getTitle, listDraftDto.getSearchTitle());
        draftQuery.in(!CollectionUtils.isEmpty(listDraftDto.getDraftIdList()), Draft::getDraftId, listDraftDto.getDraftIdList());
        draftQuery.orderByDesc(Draft::getCreateTime);
        return draftMapper.selectList(draftQuery);
    }

    @Override
    public Draft getByDraftId(String draftId) {
        List<Draft> draftList = this.listByDto(ListDraftDto.builder().draftId(draftId).build());
        if (CollectionUtils.isEmpty(draftList)) {
            return null;
        }
        return draftList.get(0);
    }

    @Override
    public Draft getByDraftIdAndUserId(String draftId, String userId) {
        List<Draft> draftList = this.listByDto(ListDraftDto.builder().draftId(draftId).userId(userId).build());
        if (CollectionUtils.isEmpty(draftList)) {
            return null;
        }
        return draftList.get(0);
    }


    @Override
    public Long countDraft(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return 0L;
        }
        LambdaQueryWrapper<Draft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Draft::getUserId, userId);
        return draftMapper.selectCount(queryWrapper);
    }


}
