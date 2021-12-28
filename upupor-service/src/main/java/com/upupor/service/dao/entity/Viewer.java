package com.upupor.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.service.common.CcConstant;
import lombok.Data;

import java.util.Date;

/**
 * 访问者
 */
@Data
public class Viewer extends BaseEntity {

    private String targetId;

    private String viewerUserId;

    private Integer targetType;

    private Integer deleteStatus;

    private Date sysUpdateTime;

    private Long createTime;

    /****************************/
    /**
     * 访问者用户头像
     */
    @TableField(exist = false)
    private String viewerUserVia = CcConstant.DEFAULT_PROFILE_PHOTO;

    /**
     * 渲染url
     */
    @TableField(exist = false)
    private String cardHtml;

    /**
     * 渲染url
     */
    @TableField(exist = false)
    private String viewerUserName;


}
