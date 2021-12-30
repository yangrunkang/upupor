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

package com.upupor.service.scheduled;

import com.alibaba.fastjson.JSON;
import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.dto.page.common.CountTagDto;
import com.upupor.service.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.upupor.service.common.CcConstant.CvCache.TAG_COUNT;


/**
 * 统计标签数
 *
 * @author: cruise
 * @created: 2020/08/12 10:34
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CountTagScheduled {

    private final ContentService contentService;

    /**
     * 每天凌晨1点执行一次：0 0 1 * * ?
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void refreshTag() {

        log.info("刷新标签云任务启动");

        List<CountTagDto> countTagDtos = contentService.listAllTag();
        if (CollectionUtils.isEmpty(countTagDtos)) {
            return;
        }

        RedisUtil.set(TAG_COUNT, JSON.toJSONString(countTagDtos));
    }

}
