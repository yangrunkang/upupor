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

package com.upupor.service.scheduled;

import com.alibaba.fastjson2.JSON;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.security.sensitive.SensitiveWord;
import com.upupor.service.data.dao.entity.BusinessConfig;
import com.upupor.service.data.service.BusinessConfigService;
import com.upupor.service.types.BusinessConfigType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.upupor.framework.CcRedisKey.CACHE_SENSITIVE_WORD;


/**
 * @description: 用户相关的定时任务
 * @author: cruise
 * @create: 2022-04-27 20:54
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemScheduled {

    private final BusinessConfigService businessConfigService;


    /**
     * 刷新敏感词
     * 每 1 分钟更新一次缓存
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void refreshSensitiveWord() {
        log.info("刷新敏感词");
        List<BusinessConfig> businessConfigList = businessConfigService.listByBusinessConfigType(BusinessConfigType.SENSITIVE_WORD);
        if (CollectionUtils.isEmpty(businessConfigList)) {
            return;
        }
        List<String> wordList = businessConfigList.stream().map(BusinessConfig::getValue)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());

        SensitiveWord sensitiveWord = new SensitiveWord();
        sensitiveWord.setWordList(wordList);
        RedisUtil.set(CACHE_SENSITIVE_WORD, JSON.toJSONString(sensitiveWord));
    }

}
