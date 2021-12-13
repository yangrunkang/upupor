package com.upupor.web.page;

import com.upupor.service.common.CcTemplateConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


/**
 * 测试邮件
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 16:26
 */
@Api(tags = "邮件模板")
@RestController
@RequiredArgsConstructor
public class TestEmailJumpController {


    @ApiOperation("邮件模板")
    @GetMapping("/email/{emailTemplate}")
    public ModelAndView emailTemplate(@PathVariable("emailTemplate") String emailTemplate) {

        String templateDic = "email/";
        String viewName = templateDic + emailTemplate;

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject(CcTemplateConstant.TITLE, "测试验证码CodeContent");
        modelAndView.addObject(CcTemplateConstant.CONTENT, "测试验证码Code");


        modelAndView.setViewName(viewName);
        return modelAndView;
    }


}
