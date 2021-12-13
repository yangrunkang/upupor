package com.upupor.service.dto.page.ad;

import com.upupor.service.dao.entity.Ad;
import lombok.Data;

/**
 * 顶栏广告
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/25 23:26
 */
@Data
public class AllTopAdDto {
    private Ad ad;

    public AllTopAdDto() {
        this.ad = new Ad();
    }

}
