package com.upupor.web.page;

import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Tag;
import com.upupor.service.service.TagService;
import com.upupor.service.service.aggregation.CommonAggregateService;
import com.upupor.spi.req.GetCommonReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.CONTENT_LIST;
import static com.upupor.service.common.CcConstant.RIGHT_ARROW;


/**
 * 公共页面跳转
 *
 * @author YangRunkang(cruise)
 * @date 2020/05/14 23:45
 */
@Api(tags = "公共页面跳转")
@RestController
@RequiredArgsConstructor
public class CommonPageJumpController {

    private final TagService tagService;

    private final CommonAggregateService commonAggregateService;

    @ApiOperation("首页")
    @GetMapping("/")
    public ModelAndView index(Integer pageNum, Integer pageSize) {
        ModelAndView modelAndView = new ModelAndView();
        GetCommonReq commonReq =
                GetCommonReq.create(null, null, pageNum, pageSize, null);
        modelAndView.addObject(commonAggregateService.index(commonReq));
        modelAndView.setViewName(CONTENT_LIST);
        return modelAndView;
    }


    @ApiOperation("短内容页面")
    @GetMapping("/topic")
    public ModelAndView topic(Integer pageNum, Integer pageSize) {
        GetCommonReq commonReq =
                GetCommonReq.create(null, null, pageNum, pageSize, CcEnum.ContentType.SHORT_CONTENT.getType());
        return contentList(commonReq);
    }

    @ApiOperation("问答")
    @GetMapping(value = {"/qa", "/qa/{tagId}", "/qa/{tagId}/{tagInId}"})
    public ModelAndView qa(Integer pageNum, Integer pageSize,
                           @PathVariable(value = "tagId", required = false) String tagId,
                           @PathVariable(value = "tagInId", required = false) String tagInId) {
        GetCommonReq commonReq =
                GetCommonReq.create(tagId, tagInId, pageNum, pageSize, CcEnum.ContentType.QA.getType());
        return getModelAndView(commonReq);
    }

    @ApiOperation("记录")
    @GetMapping(value = {"/record", "/record/{tagId}"})
    public ModelAndView record(Integer pageNum, Integer pageSize,
                               @PathVariable(value = "tagId", required = false) String tagId
    ) {
        GetCommonReq commonReq =
                GetCommonReq.create(tagId, null, pageNum, pageSize, CcEnum.ContentType.RECORD.getType());
        return getModelAndView(commonReq);
    }

    @ApiOperation("分享")
    @GetMapping(value = {"/share", "/share/{tagId}", "/share/{tagId}/{tagInId}"})
    public ModelAndView share(Integer pageNum, Integer pageSize,
                              @PathVariable(value = "tagId", required = false) String tagId,
                              @PathVariable(value = "tagInId", required = false) String tagInId) {
        GetCommonReq commonReq =
                GetCommonReq.create(tagId, tagInId, pageNum, pageSize, CcEnum.ContentType.SHARE.getType());
        return getModelAndView(commonReq);
    }

    @ApiOperation("技术")
    @GetMapping(value = {"/tech", "/tech/{tagId}", "/tech/{tagId}/{tagInId}"})
    public ModelAndView tagInChild(Integer pageNum, Integer pageSize,
                                   @PathVariable(value = "tagId", required = false) String tagId,
                                   @PathVariable(value = "tagInId", required = false) String tagInId) {
        GetCommonReq commonReq =
                GetCommonReq.create(tagId, tagInId, pageNum, pageSize, CcEnum.ContentType.TECH.getType());

        return getModelAndView(commonReq);
    }


    @ApiOperation("职场")
    @GetMapping(value = {"/workplace", "/workplace/{tagId}"})
    public ModelAndView workMenu(Integer pageNum, Integer pageSize,
                                 @PathVariable(value = "tagId", required = false) String tagId) {
        GetCommonReq commonReq =
                GetCommonReq.create(tagId, null, pageNum, pageSize, CcEnum.ContentType.WORKPLACE.getType());
        return getModelAndView(commonReq);
    }


    private ModelAndView getModelAndView(GetCommonReq commonReq) {

        ModelAndView mv = contentList(commonReq);
        // 绑定一级文章标题
        bindingFirstTitle(commonReq.getTagId(), mv);

        return mv;
    }

    private boolean bindingFirstTitle(String tagId, ModelAndView mv) {
        List<Tag> tagList = tagService.listByTagIdList(Collections.singletonList(tagId));
        if (CollectionUtils.isEmpty(tagList)) {
            return true;
        }

        Object title = mv.getModelMap().getAttribute(CcConstant.SeoKey.TITLE);
        if (Objects.isNull(title)) {
            return true;
        }

        title = title + RIGHT_ARROW + tagList.get(0).getTagName();
        mv.addObject(CcConstant.SeoKey.TITLE, title);
        mv.addObject(CcConstant.SeoKey.DESCRIPTION, title);
        return false;
    }

    /**
     * 绑定文章列表
     *
     * @param commonReq
     * @return
     */
    private ModelAndView contentList(GetCommonReq commonReq) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(commonAggregateService.index(commonReq));
        modelAndView.addObject(CcConstant.SeoKey.TITLE, CcEnum.ContentType.getName(commonReq.getContentType()));
        modelAndView.setViewName(CONTENT_LIST);
        return modelAndView;
    }

}
