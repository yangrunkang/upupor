package com.upupor.service.business.manage;

import lombok.Builder;
import lombok.Data;

/**
 * 管理统一的Dto
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Builder
@Data
public class ManageDto {
    private String applyId;
    private Integer pageNum;
    private Integer pageSize;
    private String searchTitle;
    private String userId;
    private String select;
    // 个人主页Message的状态
    private String messageStatus;
}
