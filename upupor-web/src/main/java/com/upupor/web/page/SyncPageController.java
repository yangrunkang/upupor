package com.upupor.web.page;

import com.upupor.service.dao.entity.Content;
import com.upupor.service.dto.page.ContentIndexDto;
import com.upupor.service.service.aggregation.service.ContentService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

import static com.upupor.service.common.CcConstant.CONTENT_INDEX;
import static com.upupor.service.common.CcConstant.TWO_COLONS;


/**
 * @author YangRunkang(cruise)
 * @date 2020/10/25 00:38
 */
@Api(tags = "异步刷新控制器")
@RestController
@RequiredArgsConstructor
public class SyncPageController {

    private final ContentService contentService;


    @GetMapping("sync/content/like")
    public ModelAndView work(ModelAndView model, String contentId) {
        Content content = contentService.getContentByContentIdNoStatus(contentId);
        if (Objects.isNull(content)) {
            return null;
        }

        contentService.bindLikesMember(content);

        ContentIndexDto contentIndexDto = new ContentIndexDto();
        contentIndexDto.setContent(content);


        model.addObject(contentIndexDto);
        model.setViewName(CONTENT_INDEX + TWO_COLONS + "sync_like_area");

        return model;
    }

}
