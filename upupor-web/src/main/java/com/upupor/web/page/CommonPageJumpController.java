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
        GetCommonReq commonReq = getCommonReq(pageNum, pageSize);
        modelAndView.addObject(commonAggregateService.index(commonReq));
        modelAndView.setViewName(CONTENT_LIST);
        return modelAndView;
    }


    @ApiOperation("短内容页面")
    @GetMapping("/topic")
    public ModelAndView topic(Integer pageNum, Integer pageSize) {
        GetCommonReq commonReq = getCommonReq(pageNum, pageSize, CcEnum.ContentType.SHORT_CONTENT.getType());
        return bindingContentList(commonReq);
    }

    @ApiOperation("问答")
    @GetMapping(value = {"/qa", "/qa/{tagId}", "/qa/{tagId}/{tagInId}"})
    public ModelAndView qa(Integer pageNum, Integer pageSize,
                           @PathVariable(value = "tagId", required = false) String tagId,
                           @PathVariable(value = "tagInId", required = false) String tagInId) {
        GetCommonReq commonReq = getCommonReq(pageNum, pageSize, tagId, tagInId, CcEnum.ContentType.QA.getType());
        return getModelAndView(commonReq, tagId, tagInId);
    }

    @ApiOperation("记录")
    @GetMapping(value = {"/record", "/record/{tagId}"})
    public ModelAndView record(Integer pageNum, Integer pageSize,
                               @PathVariable(value = "tagId", required = false) String tagId
    ) {
        GetCommonReq commonReq = getCommonReq(pageNum, pageSize, tagId, CcEnum.ContentType.RECORD.getType());
        return getModelAndView(commonReq, tagId);
    }

    @ApiOperation("分享")
    @GetMapping(value = {"/share", "/share/{tagId}", "/share/{tagId}/{tagInId}"})
    public ModelAndView share(Integer pageNum, Integer pageSize,
                              @PathVariable(value = "tagId", required = false) String tagId,
                              @PathVariable(value = "tagInId", required = false) String tagInId) {
        GetCommonReq commonReq = getCommonReq(pageNum, pageSize, tagId, tagInId, CcEnum.ContentType.SHARE.getType());
        return getModelAndView(commonReq, tagId, tagInId);
    }

    @ApiOperation("技术")
    @GetMapping(value = {"/tech", "/tech/{tagId}", "/tech/{tagId}/{tagInId}"})
    public ModelAndView tagInChild(Integer pageNum, Integer pageSize,
                                   @PathVariable(value = "tagId", required = false) String tagId,
                                   @PathVariable(value = "tagInId", required = false) String tagInId) {
        GetCommonReq commonReq = getCommonReq(pageNum, pageSize, tagId, tagInId, CcEnum.ContentType.TECH.getType());
        return getModelAndView(commonReq, tagId, tagInId);
    }


    @ApiOperation("职场")
    @GetMapping(value = {"/workplace", "/workplace/{tagId}"})
    public ModelAndView workMenu(Integer pageNum, Integer pageSize,
                                 @PathVariable(value = "tagId", required = false) String tagId) {
        GetCommonReq commonReq = getCommonReq(pageNum, pageSize, tagId, CcEnum.ContentType.WORKPLACE.getType());
        return getModelAndView(commonReq, tagId);
    }


    private GetCommonReq getCommonReq(Integer pageNum, Integer pageSize, String tagId, String tagInId, Integer contentType) {
        GetCommonReq commonReq = getCommonReq(pageNum, pageSize, contentType);
        commonReq.setTagId(tagId);
        commonReq.setTagInId(tagInId);
        return commonReq;
    }

    private GetCommonReq getCommonReq(Integer pageNum, Integer pageSize, String tagId, Integer contentType) {
        GetCommonReq commonReq = getCommonReq(pageNum, pageSize, contentType);
        commonReq.setTagId(tagId);
        return commonReq;
    }

    private GetCommonReq getCommonReq(Integer pageNum, Integer pageSize, Integer contentType) {
        GetCommonReq commonReq = getCommonReq(pageNum, pageSize);
        commonReq.setContentType(contentType);
        return commonReq;
    }

    private GetCommonReq getCommonReq(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        GetCommonReq commonReq = new GetCommonReq();
        commonReq.setPageNum(pageNum);
        commonReq.setPageSize(pageSize);
        return commonReq;
    }

    private ModelAndView getModelAndView(GetCommonReq commonReq, String tagId) {

        ModelAndView mv = bindingContentList(commonReq);
        // 绑定一级文章标题
        if (bindingFirstTitle(tagId, mv)) {
            return mv;
        }

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

    private ModelAndView getModelAndView(GetCommonReq commonReq, String tagId, String tagInId) {
        return getModelAndView(commonReq, tagId);
    }


    /**
     * 绑定文章列表
     *
     * @param commonReq
     * @return
     */
    private ModelAndView bindingContentList(GetCommonReq commonReq) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(commonAggregateService.index(commonReq));
        modelAndView.addObject(CcConstant.SeoKey.TITLE, CcEnum.ContentType.getName(commonReq.getContentType()));
        modelAndView.setViewName(CONTENT_LIST);
        return modelAndView;
    }

}
