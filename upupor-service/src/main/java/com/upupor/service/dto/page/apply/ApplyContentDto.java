package com.upupor.service.dto.page.apply;

import lombok.Data;

/**
 * 申请内容Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/24 21:03
 */
@Data
public class ApplyContentDto {
    /**
     * 申请说明
     */
    private String applyIntro;

    /**
     * 申请项目
     */
    private String applyProject;

}