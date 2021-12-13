package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.ContentData;
import com.upupor.service.dto.dao.HottestContentDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContentDataMapper extends BaseMapper<ContentData> {



    /**
     * 获取热门的文章
     */
    List<HottestContentDto> getHottestContentIdList(@Param("contentType") Integer contentType);

    /**
     * 获取最新评论的文章
     */
    List<String> getLatestComments(@Param("contentType") Integer contentType);

    /**
     * 浏览总数
     *
     * @return
     */
    Integer viewTotal();


}
