package com.upupor.service.service.viewhistory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dao.entity.ViewHistory;
import com.upupor.service.dao.mapper.RadioMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cruise
 * @createTime 2021-12-28 18:49
 */
@Component
public class RadioViewHistory extends AbstractViewHistory<Radio> {

    @Resource
    private RadioMapper radioMapper;


    @Override
    public CcEnum.ViewTargetType viewTargetType() {
        return CcEnum.ViewTargetType.RADIO;
    }

    @Override
    public void setViewHistoryTitleAndUrl() {
        for (ViewHistory viewHistory : getSpecifyViewHistory()) {
            for (Radio radio : getTargetList()) {
                if (radio.getRadioId().equals(viewHistory.getTargetId())) {
                    viewHistory.setTitle(radio.getRadioIntro());
                    viewHistory.setUrl("/r/" + radio.getRadioId());
                }
            }
        }
    }

    @Override
    public List<Radio> getTargetList() {
        LambdaQueryWrapper<Radio> query = new LambdaQueryWrapper<Radio>()
                .in(Radio::getRadioId, getViewHistoryTargetIdList());
        return radioMapper.selectList(query);
    }

}
