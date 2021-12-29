package com.upupor.service.manage.business;

import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Collect;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dto.page.common.ListCollectDto;
import com.upupor.service.manage.AbstractManageInfoGet;
import com.upupor.service.manage.ManageDto;
import com.upupor.service.service.aggregation.service.CollectService;
import com.upupor.service.service.aggregation.service.ContentService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cruise
 * @createTime 2021-12-24 18:03
 */
@Component
public class CollectManage extends AbstractManageInfoGet {
    @Resource
    private CollectService collectService;
    @Resource
    private ContentService contentService;

    @Override
    protected void specifyDtoHandle(ManageDto manageDto) {
        Integer pageNum = manageDto.getPageNum();
        Integer pageSize = manageDto.getPageSize();
        String userId = manageDto.getUserId();

        ListCollectDto listCollectDto = collectService.listByUserIdManage(userId, pageNum, pageSize);

        // 封装被收藏的对象
        handleCollectContent(listCollectDto);

        getMemberIndexDto().setListCollectDto(listCollectDto);

    }


    private void handleCollectContent(ListCollectDto listCollectDto) {
        if (Objects.isNull(listCollectDto)) {
            return;
        }

        if (CollectionUtils.isEmpty(listCollectDto.getCollectList())) {
            return;
        }

        List<String> contentIdList = listCollectDto.getCollectList().stream()
                .filter(collect -> collect.getCollectType().equals(CcEnum.CollectType.CONTENT.getType()))
                .map(Collect::getCollectValue).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(contentIdList)) {
            return;
        }
        List<Content> contents = contentService.listByContentIdList(contentIdList);
        if (CollectionUtils.isEmpty(contents)) {
            return;
        }
        listCollectDto.getCollectList().forEach(collect -> {
            contents.forEach(content -> {
                if (collect.getCollectValue().equals(content.getContentId())) {
                    collect.setContent(content);
                }
            });
        });
    }
}