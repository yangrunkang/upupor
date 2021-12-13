package com.upupor.service.luence.dto;

import com.upupor.service.luence.abstracts.BaseLuenceDto;
import lombok.Data;

/**
 * 内容Dto
 *
 * @author YangRunkang(cruise)
 * @date 2021/06/11 22:33
 */
@Data
public class ContentDto extends BaseLuenceDto {
    private String contentId;
    private String titleIntroContent;
}
