package com.upupor.service.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import lombok.Data;

import java.util.Date;

/**
 * 申请
 *
 * @author runkangyang
 */
@Data
public class Apply extends BaseEntity {

    private String applyId;

    private String userId;

    private Integer applySource;

    private Integer applyStatus;

    private String applyUserName;

    private String applyUserPhone;

    private String applyUserEmail;

    private String applyUserQq;

    private String applyUserWechat;

    private Long createTime;

    private Date sysUpdateTime;

    private String applyContent;

    private String replyContent;

    @TableField(exist = false)
    private String createDate;
    @TableField(exist = false)
    private String createDateDiff;

    public String getCreateDate() {
        return CcDateUtil.timeStamp2Date(createTime);
    }

    @JSONField(serialize = false)
    public String getCreateDateDiff() {
        return CcDateUtil.snsFormat(createTime);
    }
}
