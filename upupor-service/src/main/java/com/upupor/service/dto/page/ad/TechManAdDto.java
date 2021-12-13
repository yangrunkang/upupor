package com.upupor.service.dto.page.ad;

import com.upupor.service.dao.entity.Ad;
import lombok.Data;

/**
 * 技术人Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/25 23:27
 */
@Data
public class TechManAdDto {
    private Ad ad;

    public TechManAdDto() {
        this.ad = new Ad();
    }
}
