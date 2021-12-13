package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Banner;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Banner数据集合DTO
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/04 11:53
 */
@Data
public class ListBannerDto extends BaseListDto {

    private List<Banner> bannerList;

    public ListBannerDto(PageInfo pageInfo) {
        super(pageInfo);
        this.bannerList = new ArrayList<>();
    }

    public ListBannerDto() {
        this.bannerList = new ArrayList<>();
    }
}
