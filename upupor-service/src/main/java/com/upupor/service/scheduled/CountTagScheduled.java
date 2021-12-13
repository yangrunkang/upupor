package com.upupor.service.scheduled;

import com.alibaba.fastjson.JSON;
import com.upupor.service.dto.page.common.CountTagDto;
import com.upupor.service.service.ContentService;
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
