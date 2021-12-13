package com.upupor.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import lombok.Data;

import java.util.Date;

/**
 * 用户积分
 *
 * @author runkangyang (cruise)
 * @date 2020.03.08 09:40
 */
@Data
public class MemberIntegral {
    private Long id;

    private String integralId;

    private String integralUserId;

    private String targetId;

    private Long integralRuleId;

    private Long integralValue;

    private String integralText;

    private Integer status;

    private String remark;

    private Long createTime;

    private Date sysUpdateTime;

    @TableField(exist = false)
    private String createDate;

    public String getCreateDate() {
        return CcDateUtil.timeStamp2Date(createTime);
    }
}
