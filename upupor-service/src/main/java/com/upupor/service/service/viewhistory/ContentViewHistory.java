package com.upupor.service.service.viewhistory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.ViewHistory;
import com.upupor.service.dao.mapper.ContentMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cruise
 * @createTime 2021-12-28 18:48
 */
@Component
public class ContentViewHistory extends AbstractViewHistory<Content> {

    @Resource
    private ContentMapper contentMapper;

    @Override
    public CcEnum.ViewTargetType viewTargetType() {
        return CcEnum.ViewTargetType.CONTENT;
    }

    @Override
    public List<Content> getTargetList() {
        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
                .in(Content::getContentId, getViewHistoryTargetIdList());
        return contentMapper.selectList(query);
    }

    @Override
    public void setViewHistoryTitleAndUrl() {
        for (ViewHistory viewHistory : getSpecifyViewHistory()) {
            for (Content content : getTargetList()) {
                if (content.getContentId().equals(viewHistory.getTargetId())) {
                    viewHistory.setTitle(content.getTitle());
                    viewHistory.setUrl("/u/" + content.getContentId());
                }
            }
        }
    }

}
