package com.upupor.service.dto.page.ad;

import com.upupor.service.dao.entity.Ad;
import lombok.Data;

/**
 * 分享页面广告
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/28 10:26
 */
@Data
public class ShareAdDto {
    private Ad ad;

    public ShareAdDto() {
        this.ad = new Ad();
    }
}
