package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Banner;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BannerMapper extends BaseMapper<Banner> {



    List<Banner> listBannerByStatus(@Param("bannerStatus") Integer bannerStatus);

}
