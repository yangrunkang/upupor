package com.upupor.service.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import lombok.Data;

import java.util.Date;

/**
 * 收藏
 *
 * @author runkangyang (cruise)
 * @date 2020.01.27 15:08
 */
@Data
public class Collect extends BaseEntity {

    private String collectId;

    private String userId;

    private String collectValue;

    private Integer collectType;

    private Integer status;

    private Long createTime;

    private Date sysUpdateTime;

    @TableField(exist = false)
    private String createDate;

    @TableField(exist = false)
    private String createDateDiff;

    /**
     * 收藏对象——内容
     */
    @TableField(exist = false)
    private Content content;

    public String getCreateDate() {
        return CcDateUtil.timeStamp2Date(createTime);
    }

    @JSONField(serialize = false)
    public String getCreateDateDiff() {
        return CcDateUtil.snsFormat(createTime);
    }

}
