package com.upupor.web.page;

import com.upupor.service.business.aggregation.AdminAggregateService;
import com.upupor.service.common.CcConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

import static com.upupor.service.common.CcConstant.*;

/**
 * 用户页面跳转控制器
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/19 12:16
 */
@Slf4j
@Api(tags = "用户页面跳转控制器")
@RestController
@RequiredArgsConstructor
public class AdminPageJumpController {

    private final AdminAggregateService adminAggregateService;

    @ApiOperation("管理员-管理员")
    @GetMapping("/user/admin/admin")
    public ModelAndView userManageAdmin(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDIT_USER_MANAGE_ADMIN);
        modelAndView.addObject(adminAggregateService.admin(pageNum, pageSize));
        modelAndView.addObject(SeoKey.TITLE, "管理员");
        modelAndView.addObject(SeoKey.DESCRIPTION, "管理员");
        return modelAndView;
    }

    @ApiOperation("管理员-管理文章")
    @GetMapping("/user/admin/content/{contentId}")
    public ModelAndView userManageAdmin(Integer pageNum, Integer pageSize, @PathVariable("contentId") String contentId) {

        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDIT_USER_MANAGE_CONTENT);
        modelAndView.addObject(adminAggregateService.adminContent(pageNum, pageSize, contentId));
        modelAndView.addObject(SeoKey.TITLE, "管理员");
        modelAndView.addObject(SeoKey.DESCRIPTION, "管理员");
        return modelAndView;
    }


}
