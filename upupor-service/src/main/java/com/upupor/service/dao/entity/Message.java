package com.upupor.service.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import lombok.Data;

import java.util.Date;

/**
 * @author runkangyang
 */
@Data
public class Message extends BaseEntity {

    private String messageId;

    private String userId;

    private Integer messageType;

    private String message;

    private Integer status;

    private Long createTime;

    private Date sysUpdateTime;

    /**
     * 冗余字段
     */
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
