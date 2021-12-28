package com.upupor.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
public class Member extends BaseEntity {

    private String userId;

    private String userName;

    private Integer status;

    private String email;

    private String phone;

    private String password;

    private String via;

    private Integer statementId;

    /**
     * 是否是管理员 0-非管理源 1-管理员
     */
    private Integer isAdmin;

    private Long createTime;
    /**
     * 最近登录时间
     */
    private Long lastLoginTime;

    /**
     * 系统更新时间
     */
    private Date sysUpdateTime;
/********************************************/
    /**
     * 页面冗余字段
     */
    @TableField(exist = false)
    private String createDate;
    @TableField(exist = false)
    private String createDateDiff;
    @TableField(exist = false)
    private String lastLoginDate;
    /**
     * 创建时间短 年-月-日
     */
    @TableField(exist = false)
    private String createDateShort;

    /**
     * 页面冗余字段
     */
    @TableField(exist = false)
    private String sysUpdateDate;

    /**
     * 粉丝数
     */
    @TableField(exist = false)
    private Integer fansNum;

    /**
     * 关注数
     */
    @TableField(exist = false)
    private Integer attentionNum;

    /**
     * 积分总数
     */
    @TableField(exist = false)
    private Integer totalIntegral;

    /**
     * 文章总数
     */
    @TableField(exist = false)
    private Integer totalContentNum;

    /**
     * 用户拓展信息
     */
    @TableField(exist = false)
    private MemberExtend memberExtend;

    /**
     * 用户配置信息
     */
    @TableField(exist = false)
    private MemberConfig memberConfig;

    @TableField(exist = false)
    private Statement statement;

    /**
     * 感谢点赞的html
     */
    @TableField(exist = false)
    private String cardHtml;

    /**
     *
     */
    @TableField(exist = false)
    private List<String> historyViaList;

    public Member() {
        this.fansNum = 0;
        this.attentionNum = 0;
        this.totalIntegral = 0;
        this.historyViaList = new ArrayList<>();
    }

    public String getCreateDate() {
        if (Objects.nonNull(createTime) && createTime > 0) {
            return CcDateUtil.timeStamp2Date(createTime);
        }
        return createDate;
    }

    public String getLastLoginDate() {
        if (Objects.nonNull(lastLoginTime) && lastLoginTime > 0) {
            return CcDateUtil.timeStamp2Date(lastLoginTime);
        }
        return lastLoginDate;
    }

    public String getCreateDateShort() {
        if (Objects.nonNull(createTime) && createTime > 0) {
            return CcDateUtil.timeStamp2DateShort(createTime);
        }
        return createDate;
    }

    public String getVia() {
        return via;
    }

}
