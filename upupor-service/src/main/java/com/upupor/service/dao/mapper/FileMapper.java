package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.File;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileMapper extends BaseMapper<File> {

    File selectByMd5(@Param("md5") String md5);

    File selectByFileUrl(@Param("fileUrl") String fileUrl);

    /**
     * 用户历史头像
     *
     * @param userId
     * @return
     */
    List<String> historyVia(@Param("userId") String userId);

}
