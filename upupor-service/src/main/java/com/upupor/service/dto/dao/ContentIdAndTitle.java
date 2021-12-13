package com.upupor.service.dto.dao;

import lombok.Data;

/**
 * @author YangRunkang(cruise)
 * @date 2020/11/15 01:54
 */
@Data
public class ContentIdAndTitle {
    private String contentId;
    private String title;
    private Long latestCommentTime;

}
