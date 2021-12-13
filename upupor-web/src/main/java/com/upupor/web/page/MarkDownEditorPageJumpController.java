package com.upupor.web.page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import static com.upupor.service.common.CcConstant.EDITOR_MD;
import static com.upupor.service.common.CcConstant.SeoKey;


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
public class MarkDownEditorPageJumpController {

    @ApiOperation("markdown编辑器")
    @GetMapping("/editor/md")
    public ModelAndView editor() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDITOR_MD);
        modelAndView.addObject(SeoKey.TITLE, "创建内容");
        return modelAndView;
    }


}
