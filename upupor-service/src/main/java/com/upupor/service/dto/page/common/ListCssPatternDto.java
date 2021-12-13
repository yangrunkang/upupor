package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.CssPattern;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Css 模式 DTO
 *
 * @author YangRunkang(cruise)
 * @date 2020/10/02 21:21
 */
@Data
public class ListCssPatternDto extends BaseListDto {
    /**
     * Css模式集合
     */
    private List<CssPattern> patternList;

    private CssPattern userDefinedCss;

    public ListCssPatternDto(PageInfo pageInfo) {
        super(pageInfo);
        this.patternList = new ArrayList<>();
    }

    public ListCssPatternDto() {
        this.patternList = new ArrayList<>();
    }

    public List<CssPattern> getPatternList() {
        if (CollectionUtils.isEmpty(patternList)) {
            return new ArrayList<>();
        }
        return patternList;
    }

}
