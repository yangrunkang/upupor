package com.upupor.service.dto.page;

import com.upupor.service.dto.page.ad.*;
import lombok.Data;

/**
 * 广告服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/25 23:26
 */
@Data
public class AdIndexDto {

    private AllBottomAdDto allBottomAdDto;
    private AllTopAdDto allTopAdDto;
    private QaAdDto qaAdDto;
    private ShareAdDto shareAdDto;
    private TechManAdDto techManAdDto;
    private TechRightAdDto techRightAdDto;
    private TechRightClassAdDto techRightClassAdDto;
    private TechRightPromoteAdDto techRightPromoteAdDto;
    private WorkplaceAdDto workplaceAdDto;

    public AdIndexDto() {
        this.allBottomAdDto = new AllBottomAdDto();
        this.allTopAdDto = new AllTopAdDto();
        this.qaAdDto = new QaAdDto();
        this.shareAdDto = new ShareAdDto();
        this.techManAdDto = new TechManAdDto();
        this.techRightAdDto = new TechRightAdDto();
        this.techRightClassAdDto = new TechRightClassAdDto();
        this.techRightPromoteAdDto = new TechRightPromoteAdDto();
        this.workplaceAdDto = new WorkplaceAdDto();
    }
}
