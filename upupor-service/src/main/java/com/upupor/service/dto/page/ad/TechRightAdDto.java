package com.upupor.service.dto.page.ad;

import com.upupor.service.dao.entity.Ad;
import lombok.Data;

/**
 * 技术页广告
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/28 10:26
 */
@Data
public class TechRightAdDto {
    private Ad ad;

    public TechRightAdDto() {
        this.ad = new Ad();
    }
}
