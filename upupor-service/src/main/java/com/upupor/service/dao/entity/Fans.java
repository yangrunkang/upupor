package com.upupor.service.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import lombok.Data;

import java.util.Date;

@Data
public class Fans extends BaseEntity {

    private String fanId;

    private String userId;

    private String fanUserId;

    private Integer fanStatus;

    private Long createTime;

    private Date sysUpdateTime;

    @TableField(exist = false)
    private Member member;

    /**
     * 页面冗余字段
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
