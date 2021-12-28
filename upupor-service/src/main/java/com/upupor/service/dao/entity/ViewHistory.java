package com.upupor.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.CcConstant;
import lombok.Data;

import java.util.Date;

/**
 * 浏览记录
 */
@Data
public class ViewHistory {

    private Long id;

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

    /**
     * 浏览标题
     */
    @TableField(exist = false)
    private String title;

    /**
     * 来源
     */
    @TableField(exist = false)
    private String source;

    /**
     * 浏览url
     */
    @TableField(exist = false)
    private String url;

    /**
     * 创建时间
     */
    @TableField(exist = false)
    private String createDate;

    public String getCreateDate() {
        return CcDateUtil.timeStamp2Date(createTime);
    }
}
