package com.upupor.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

/**
 * 待办
 */

@Data
public class Todo {
    private Long id;

    private String userId;

    private String todoId;

    private String title;

    private Long startTime;

    private Long endTime;

    private Integer status;

    private Long createTime;

    private Date sysUpdateTime;

    /**
     * 明细
     */
    @TableField(exist = false)
    private TodoDetail todoDetail;

    /**
     * 用户
     */
    @TableField(exist = false)
    private Member member;


    @TableField(exist = false)
    private String createDate;

    public String getCreateDate() {
        if (Objects.nonNull(createTime) && createTime > 0) {
            return CcDateUtil.timeStamp2Date(createTime);
        }
        return createDate;
    }

}
