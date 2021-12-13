package com.upupor.service.dto.page.ad;

import com.upupor.service.dao.entity.Ad;
import lombok.Data;

/**
 * 问答页广告
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/28 10:26
 */
@Data
public class QaAdDto {
    private Ad ad;

    public QaAdDto() {
        this.ad = new Ad();
    }
}
