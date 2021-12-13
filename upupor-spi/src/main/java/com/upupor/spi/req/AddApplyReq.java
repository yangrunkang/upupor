package com.upupor.spi.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 07:36
 */
@Data
public class AddApplyReq implements Serializable {

    private String userId;

    @NotEmpty(message = "用户名不能为空")
    private String applyUserName;

    private String applyUserPhone;

    private String applyUserEmail;

    private String applyUserQq;

    private String applyUserWechat;

    @NotEmpty(message = "广告申请内容简介不能为空")
    private String adIntro;

    /**
     * 选中的广告
     */
    private String positionIdList;

    /**
     * 申请类型
     */
    private Integer type;

}
