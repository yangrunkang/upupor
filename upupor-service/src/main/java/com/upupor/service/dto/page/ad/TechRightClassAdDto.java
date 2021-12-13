package com.upupor.service.dto.page.ad;

import com.upupor.service.dao.entity.Ad;
import lombok.Data;

/**
 * 课程信息
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/25 23:27
 */
@Data
public class TechRightClassAdDto {

    private Ad ad;

    public TechRightClassAdDto() {
        this.ad = new Ad();
    }
}
