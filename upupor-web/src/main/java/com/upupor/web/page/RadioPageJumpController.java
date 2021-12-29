package com.upupor.web.page;

import com.upupor.service.business.aggregation.RadioAggregateService;
import com.upupor.service.business.aggregation.service.CommentService;
import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.business.aggregation.service.ViewerService;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dto.page.RadioIndexDto;
import com.upupor.service.utils.PageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

import static com.upupor.service.common.CcConstant.Page.SIZE_COMMENT;
import static com.upupor.service.common.CcConstant.*;


/**
 * 电台跳转
 *
 * @author YangRunkang(cruise)
 * @date 2020/11/22 23:23
 */
@Api(tags = "电台跳转")
@RestController
@RequiredArgsConstructor
public class RadioPageJumpController {

    private final RadioAggregateService radioAggregateService;

    private final CommentService commentService;

    private final ViewerService viewerService;

    private final MessageService messageService;

    @ApiOperation("电台")
    @GetMapping("/radio-station")
    public ModelAndView radioStation(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(RADIO_STATION_LIST);

        // 电台列表
        modelAndView.addObject(radioAggregateService.index(pageNum, pageSize));

        // Seo
        modelAndView.addObject(CcConstant.SeoKey.TITLE, "电台列表");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "电台,节目,分享,独立思考");
        return modelAndView;
    }

    @ApiOperation("创建电台")
    @GetMapping("/radio-station/create")
    public ModelAndView radioStationCreate() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(RADIO_STATION_CREATE);
        modelAndView.addObject(CcConstant.SeoKey.TITLE, "开始分享您的声音");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "电台,节目,分享,独立思考");
        return modelAndView;
    }

    @ApiOperation("电台")
    @GetMapping("/radio-station/record")
    public ModelAndView radioStationRecord() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(RADIO_STATION_RECORD);
        modelAndView.addObject(CcConstant.SeoKey.TITLE, "录制声音");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "电台,节目,分享,独立思考");
        return modelAndView;
    }

    @ApiOperation("电台详情")
    @GetMapping("/r/{radioId}")
    public ModelAndView radioDetail(@PathVariable("radioId") String radioId, Integer pageNum, Integer pageSize,
                                    String msgId) {
        if (Objects.isNull(pageNum)) {
            // 获取最新的评论
            Integer count = commentService.countByTargetId(radioId);
            pageNum = PageUtils.calcMaxPage(count, SIZE_COMMENT);
        }

        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE_COMMENT;
        }

        if (Objects.nonNull(msgId)) {
            messageService.tagMsgRead(msgId);
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(RADIO_STATION_INDEX);

        RadioIndexDto detail = radioAggregateService.detail(radioId, pageNum, pageSize);

        // 记录访问者
        viewerService.addViewer(radioId, CcEnum.ViewTargetType.RADIO);
        // 电台列表
        modelAndView.addObject(detail);

        // Seo
        modelAndView.addObject(CcConstant.SeoKey.TITLE, detail.getRadio().getRadioIntro());
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, detail.getRadio().getRadioIntro());
        return modelAndView;
    }


}
