package com.upupor.service.dto.page.ad;

import com.upupor.service.dao.entity.Ad;
import lombok.Data;

/**
 * 职场广告
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/28 10:27
 */
@Data
public class WorkplaceAdDto {
    private Ad ad;

    public WorkplaceAdDto() {
        this.ad = new Ad();
    }
}
