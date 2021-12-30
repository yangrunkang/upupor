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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.business.aggregation.service.CollectService;
import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.business.aggregation.service.MemberIntegralService;
import com.upupor.service.common.*;
import com.upupor.service.dao.entity.Collect;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.utils.Asserts;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.AddCollectReq;
import com.upupor.spi.req.UpdateCollectReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.MsgTemplate.CONTENT_INTEGRAL;


/**
 * 收藏控制层
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 18:24
 */
@Slf4j
@Api(tags = "收藏服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("collect")
public class CollectController {

    private final CollectService collectService;

    private final ContentService contentService;

    private final MemberIntegralService memberIntegralService;

    @PostMapping("/add")
    @ResponseBody
    @ApiOperation("添加收藏")
    public CcResponse add(AddCollectReq addCollectReq) {
        CcResponse ccResponse = new CcResponse();

        String userId = ServletUtils.getUserId();

        if (StringUtils.isEmpty(addCollectReq.getCollectValue())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        if (addCollectReq.getIsCollect()) {

            LambdaQueryWrapper<Collect> queryCollect = new LambdaQueryWrapper<Collect>()
                    .eq(Collect::getCollectValue, addCollectReq.getCollectValue())
                    .eq(Collect::getUserId, userId);

            Collect collect = collectService.select(queryCollect);
            Asserts.notNull(collect, ErrorCode.COLLECT_VIA_NOT_EXISTS);
            // 该用户的收藏取消
            collect.setUserId(userId);
            collect.setStatus(CcEnum.CollectStatus.DELETE.getStatus());
            Integer cancelCollect = collectService.update(collect);
            boolean handleSuccess = cancelCollect > 0;
            if (handleSuccess) {
                Content content = null;
                String text = null;
                if (addCollectReq.getCollectType().equals(CcEnum.CollectType.CONTENT.getType())) {
                    content = contentService.getNormalContent(addCollectReq.getCollectValue());
                }

                if (Objects.nonNull(content)) {
                    String desc = String.format(CONTENT_INTEGRAL, content.getContentId(), CcUtils.getUuId(), content.getTitle());
                    text = "您取消收藏《" + desc + "》文章,扣减积分";
                }
                memberIntegralService.reduceIntegral(IntegralEnum.COLLECT, text, userId, addCollectReq.getCollectValue());
            }
            ccResponse.setData(handleSuccess);
            return ccResponse;
        } else {
            // 添加收藏前校验自己是否有【仅自己可见】 的收藏项,如果有直接更新状态就好了
            Boolean isExists = collectService.existsCollectContent(addCollectReq.getCollectValue(), CcEnum.CollectStatus.ONLY_SELF_SEE.getStatus(), userId);
            if (isExists) {
                Collect collect = collectService.getCollect(addCollectReq.getCollectValue(), CcEnum.CollectStatus.ONLY_SELF_SEE.getStatus(), userId);
                if (Objects.isNull(collect)) {
                    throw new BusinessException(ErrorCode.COLLECT_CONTENT_ERROR);
                }
                collect.setStatus(CcEnum.CollectStatus.NORMAL.getStatus());
                Integer integer = collectService.update(collect);
                ccResponse.setData(integer > 0);
                return ccResponse;
            }

            Content content = null;
            if (addCollectReq.getCollectType().equals(CcEnum.CollectType.CONTENT.getType())) {
                content = contentService.getNormalContent(addCollectReq.getCollectValue());
                if (content.getUserId().equals(userId)) {
                    throw new BusinessException(ErrorCode.FORBIDDEN_COLLECT_SELF_CONTENT);
                }
            }

            // 收藏
            Collect collect = new Collect();
            collect.setUserId(userId);
            collect.setCollectId(CcUtils.getUuId());
            collect.setCollectType(addCollectReq.getCollectType());
            collect.setCollectValue(addCollectReq.getCollectValue());
            collect.setStatus(CcEnum.CollectStatus.NORMAL.getStatus());
            collect.setCreateTime(CcDateUtil.getCurrentTime());
            collect.setSysUpdateTime(new Date());

            Integer result = collectService.addCollect(collect);
            boolean handleSuccess = result > 0;

            if (handleSuccess) {
                String text = null;
                if (Objects.nonNull(content)) {
                    String desc = String.format(CONTENT_INTEGRAL, content.getContentId(), CcUtils.getUuId(), content.getTitle());
                    text = "您收藏了《" + desc + "》文章,增加积分";
                }
                memberIntegralService.addIntegral(IntegralEnum.COLLECT, text, userId, collect.getCollectId());

            }

            ccResponse.setData(handleSuccess);
            return ccResponse;
        }
    }

    @ApiOperation("编辑收藏")
    @PostMapping(value = "/edit")
    @ResponseBody
    public CcResponse edit(UpdateCollectReq updateCollectReq) {
        CcResponse ccResponse = new CcResponse();

        LambdaQueryWrapper<Collect> query = new LambdaQueryWrapper<Collect>()
                .eq(Collect::getCollectId, updateCollectReq.getCollectId());
        Collect collect = collectService.select(query);
        Asserts.notNull(collect, ErrorCode.COLLECT_VIA_NOT_EXISTS);
        // 更新状态
        collect.setStatus(updateCollectReq.getStatus());
        Integer result = collectService.update(collect);

        ccResponse.setData(result > 0);
        return ccResponse;
    }

}
