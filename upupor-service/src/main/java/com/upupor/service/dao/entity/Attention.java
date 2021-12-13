package com.upupor.service.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import lombok.Data;

import java.util.Date;

@Data
public class Attention {
    private Long id;

    private String attentionId;

    private String userId;

    private String attentionUserId;

    private Integer attentionStatus;

    private Long createTime;

    private Date sysUpdateTime;

    /**
     * 冗余字段
     */
    @TableField(exist = false)
    private String createDate;

    @TableField(exist = false)
    private String createDateDiff;

    /**
     * 关注/粉丝 (根据url来)
     */
    @TableField(exist = false)
    private Member member;

    public String getCreateDate() {
        return CcDateUtil.timeStamp2Date(createTime);
    }

    @JSONField(serialize = false)
    public String getCreateDateDiff() {
        return CcDateUtil.snsFormat(createTime);
    }


}
