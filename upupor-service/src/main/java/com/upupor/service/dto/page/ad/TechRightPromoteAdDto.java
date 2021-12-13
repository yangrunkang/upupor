package com.upupor.service.dto.page.ad;

import com.upupor.service.dao.entity.Ad;
import lombok.Data;

/**
 * 技术页提升广告
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/28 10:23
 */
@Data
public class TechRightPromoteAdDto {
    private Ad ad;

    public TechRightPromoteAdDto() {
        this.ad = new Ad();
    }
}
