package com.upupor.web.page;

import com.alibaba.fastjson.JSON;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.ContentExtend;
import com.upupor.service.dao.entity.Tag;
import com.upupor.service.dto.page.EditorIndexDto;
import com.upupor.service.service.TagService;
import com.upupor.service.service.aggregation.CommonAggregateService;
import com.upupor.service.service.aggregation.EditorAggregateService;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.RedisUtil;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.AddCacheContentReq;
import com.upupor.spi.req.GetEditorReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.*;


/**
 * 编辑器页面跳转控制器
 *
 * @author YangRunkang(cruise)
 * @date 2020/04/22 01:19
 */
@Slf4j
@Api(tags = "编辑器页面跳转控制器")
@RestController
@RequiredArgsConstructor
public class EditorPageJumpController {

    private final EditorAggregateService editorAggregateService;

    private final TagService tagService;

    @ApiOperation("进入编辑页之前检查是否有缓存")
    @GetMapping("/before-editor")
    public ModelAndView editor(@RequestParam(value = "opt", required = false) String opt) {


        ModelAndView modelAndView = new ModelAndView();

        String cacheContentKey = CcConstant.CvCache.CONTENT_CACHE_KEY + ServletUtils.getUserId();

        // 写新文章的话,则清除缓存,然后跳转到 before-editor.html 去选择新的文章类型
        String writeNewContent = "new";
        if (!StringUtils.isEmpty(opt) && writeNewContent.equals(opt)) {
            RedisUtil.remove(cacheContentKey);
            modelAndView.setViewName(BEFORE_EDITOR);
            modelAndView.addObject(SeoKey.TITLE, "创建内容");
            return modelAndView;
        }

        String cacheContent = RedisUtil.get(cacheContentKey);
        if (StringUtils.isEmpty(cacheContent)) {
            modelAndView.setViewName(BEFORE_EDITOR);
            modelAndView.addObject(SeoKey.TITLE, "创建内容");
        } else {
            // 跳转到系统自动保存的历史页面
            modelAndView.setViewName(CONTENT_HISTORY);
            AddCacheContentReq addCacheContentReq = JSON.parseObject(cacheContent, AddCacheContentReq.class);
            modelAndView.addObject(CvCache.CACHE_CONTENT_DATA, addCacheContentReq);
            modelAndView.addObject(SeoKey.TITLE, "上次退出未保存的内容");
            modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "上次退出未保存的内容");
        }
        return modelAndView;
    }

    /**
     * 正常流程从页面新建编辑内容
     */
    @ApiOperation("正常流程从页面新建编辑内容")
    @GetMapping("/editor")
    public ModelAndView editor(@RequestParam(value = "type", required = false) Integer contentType,
                               @RequestParam(value = "contentId", required = false) String contentId,
                               @RequestParam(value = "edit", required = false) Boolean edit,
                               @RequestParam(value = "tag", required = false) String tag
    ) {

        ModelAndView modelAndView = new ModelAndView();

        if (Objects.isNull(contentType)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "从未知入口进入编辑器");
        }

        // 从个人中心编辑内容时校验是否有缓存文章
        String cacheContentKey = CvCache.CONTENT_CACHE_KEY + ServletUtils.getUserId();
        String cacheContent = RedisUtil.get(cacheContentKey);
        if (!StringUtils.isEmpty(cacheContent)) {
            modelAndView.setViewName(CONTENT_HISTORY);
            AddCacheContentReq addCacheContentReq = JSON.parseObject(cacheContent, AddCacheContentReq.class);
            modelAndView.addObject(CvCache.CACHE_CONTENT_DATA, addCacheContentReq);
            return modelAndView;
        }

        try {
            GetEditorReq getEditorReq = new GetEditorReq();
            getEditorReq.setContentType(contentType);
            getEditorReq.setTag(tag);
            getEditorReq.setContentId(contentId);
            modelAndView.addObject(editorAggregateService.index(getEditorReq));
            // 如果是从管理后台过来的编辑,edit为true,然后依次来控制按钮是否显示或者隐藏
            modelAndView.addObject("edit", edit);
        } catch (Exception e) {
            modelAndView.addObject(CcConstant.GLOBAL_EXCEPTION, e.getMessage());
            modelAndView.setViewName(PAGE_500);
        }

        modelAndView.setViewName(EDITOR);
        modelAndView.addObject(SeoKey.TITLE, "编辑器");
        modelAndView.addObject("type", contentType);
        // 预生成 内容ID
        modelAndView.addObject("pre_content_id", CcUtils.getUuId());
        return modelAndView;
    }

    @ApiOperation("缓存文章继续编辑")
    @GetMapping("/continue-editor")
    public ModelAndView continueEditor(@RequestParam("edit") Boolean edit) {
        ModelAndView modelAndView = new ModelAndView();
        String cacheContentKey = CvCache.CONTENT_CACHE_KEY + ServletUtils.getUserId();
        String cacheContent = RedisUtil.get(cacheContentKey);
        EditorIndexDto editorIndexDto = getEditorIndexDtoFromCache(cacheContent);
        modelAndView.addObject(editorIndexDto);
        modelAndView.addObject("edit", edit);
        modelAndView.setViewName(EDITOR);
        modelAndView.addObject(SeoKey.TITLE, "编辑器");
        return modelAndView;
    }

    /**
     * 从缓存对象中获取编辑器的 EditorIndexDto
     *
     * @return
     */
    private EditorIndexDto getEditorIndexDtoFromCache(String cacheContent) {
        AddCacheContentReq addCacheContentReq = JSON.parseObject(cacheContent, AddCacheContentReq.class);
        if (Objects.isNull(addCacheContentReq)) {
            throw new BusinessException(ErrorCode.YOU_DROPPED_CACHE_CONTENT);
        }
        //
        EditorIndexDto editorIndexDto = new EditorIndexDto();
        //
        Content content = new Content();
        content.setTitle(addCacheContentReq.getTitle());
        content.setShortContent(addCacheContentReq.getShortContent());
        content.setContentType(addCacheContentReq.getContentType());
        // 如果为空也没事,反正前端按钮都不会显示
        content.setContentId(addCacheContentReq.getContentId());
        content.setUserId(addCacheContentReq.getUserId());
        // 获取到用户之前选择的标签
        content.setTagIds(addCacheContentReq.getTagIds());
        content.setOriginType(addCacheContentReq.getOriginType());
        content.setNoneOriginLink(addCacheContentReq.getNoneOriginLink());
        // 内容拓展
        ContentExtend contentExtend = new ContentExtend();
        contentExtend.setDetailContent(addCacheContentReq.getContent());
        content.setContentExtend(contentExtend);
        editorIndexDto.setContent(content);

        // 获取标签 继续编辑就获取缓存中的
        List<Tag> tagList = tagService.getTagsByType(addCacheContentReq.getContentType());
        if (CollectionUtils.isEmpty(tagList)) {
//            throw new BusinessException(ErrorCode.SYSTEM_INIT_ERROR_WITHOUT_ANY_TAGS);
        }
        editorIndexDto.setTagList(tagList);
        editorIndexDto.setCreateContentDesc(CommonAggregateService.getCreateContentInfo(addCacheContentReq.getContentType(), addCacheContentReq.getTagIds()));
        return editorIndexDto;
    }

}
