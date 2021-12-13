package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Viewer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ViewerMapper extends BaseMapper<Viewer> {

    int countByTargetIdAndViewerUserId(@Param("targetId") String targetId, @Param("viewerUserId") String viewerUserId, @Param("targetType") Integer targetType);

    List<Viewer> listByTargetId(@Param("targetId") String targetId, @Param("targetType") Integer targetType);

}
