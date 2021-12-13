package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.ContentEditReason;

public interface ContentEditReasonMapper extends BaseMapper<ContentEditReason> {

    ContentEditReason latestEditReason(String contentId);
}
