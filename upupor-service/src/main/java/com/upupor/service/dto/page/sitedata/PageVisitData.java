package com.upupor.service.dto.page.sitedata;

import lombok.Data;

/**
 * 页面访问数据
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/18 03:46
 */
@Data
public class PageVisitData {

    private Integer homeTimes;
    private Integer techTimes;
    private Integer qaTimes;
    private Integer shareTimes;
    private Integer workplaceTimes;
    private Integer photographyTimes;
    private Integer siteDataTimes;
    private Integer recordTimes;
    private Integer topicTimes;
    private Integer radioStationTimes;


    public PageVisitData() {
        this.homeTimes = 0;
        this.techTimes = 0;
        this.qaTimes = 0;
        this.shareTimes = 0;
        this.workplaceTimes = 0;
        this.photographyTimes = 0;
        this.siteDataTimes = 0;
        this.recordTimes = 0;
        this.topicTimes = 0;
        this.radioStationTimes = 0;
    }
}
