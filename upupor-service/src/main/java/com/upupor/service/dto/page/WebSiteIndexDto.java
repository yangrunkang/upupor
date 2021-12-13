package com.upupor.service.dto.page;


import com.upupor.service.dto.page.sitedata.BusinessData;
import com.upupor.service.dto.page.sitedata.PageVisitData;
import lombok.Data;

/**
 * 网站数据Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/18 03:44
 */
@Data
public class WebSiteIndexDto {

    private BusinessData businessData;

    private PageVisitData pageVisitData;

    private String dateStr;

    public WebSiteIndexDto() {
        this.businessData = new BusinessData();
        this.pageVisitData = new PageVisitData();
    }
}
