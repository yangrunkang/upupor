package com.upupor.spi.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 添加活动请求
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/05 00:14
 */
@Data
public class AddActivityReq {

    @Length(max = 256, message = "短内容标题名太长,不能超过 256 个字")
    private String activityName;

    private String activityDesc;


}
