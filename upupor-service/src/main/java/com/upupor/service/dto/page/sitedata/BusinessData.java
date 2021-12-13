package com.upupor.service.dto.page.sitedata;

import lombok.Data;

/**
 * 网站业务数据
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/18 03:46
 */
@Data
public class BusinessData {

    private Integer contentTotal;

    private Integer commentTotal;

    private Integer onlineUserTotal;

    private Integer advertisingTotal;

    private Integer loginTimes;

    private Integer registerNum;

    private Integer viewContentTotal;

    public BusinessData() {
        this.contentTotal = 0;
        this.commentTotal = 0;
        this.onlineUserTotal = 0;
        this.advertisingTotal = 0;
        this.loginTimes = 0;
        this.registerNum = 0;
        this.viewContentTotal = 0;
    }
}
