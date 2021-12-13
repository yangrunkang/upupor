package com.upupor.service.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Banner;
import com.upupor.service.dao.mapper.BannerMapper;
import com.upupor.service.dto.page.common.ListBannerDto;
import com.upupor.service.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * BannerServiceImpl
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/04 11:55
 */
@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;

    @Override
    public ListBannerDto listBannerByStatus(Integer bannerStatus, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Banner> banners = bannerMapper.listBannerByStatus(bannerStatus);
        PageInfo<Banner> pageInfo = new PageInfo<>(banners);

        ListBannerDto listBannerDto = new ListBannerDto(pageInfo);
        listBannerDto.setBannerList(pageInfo.getList());
        return listBannerDto;
    }
}
