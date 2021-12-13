package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Collect;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏服务Mapper
 *
 * @author runkangyang (cruise)
 * @date 2020.01.27 15:19
 */
public interface CollectMapper extends BaseMapper<Collect> {

    /**
     * 只是根据用户id获取 (没有判定状态)
     *
     * @param userId
     * @return
     */
    List<Collect> listByUserId(@Param("userId") String userId);

    List<Collect> listByUserIdManage(@Param("userId") String userId);



    int existsCollectContent(@Param("contentId") String contentId, @Param("userId") String userId);

    int getByContentIdAndCollectStatus(@Param("contentId") String contentId, @Param("collectStatus") Integer collectStatus, @Param("userId") String userId);

    Collect getCollect(@Param("contentId") String contentId, @Param("collectStatus") Integer collectStatus, @Param("userId") String userId);

    Integer collectNum(@Param("collectType") Integer collectType, @Param("collectValue") String collectValue);
}
