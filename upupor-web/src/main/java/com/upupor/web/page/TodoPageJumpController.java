package com.upupor.web.page;

import com.upupor.service.common.CcConstant;
import com.upupor.service.service.aggregation.TodoAggregateService;
import com.upupor.service.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

import static com.upupor.service.common.CcConstant.SeoKey;
import static com.upupor.service.common.CcConstant.TODO_INDEX;


/**
 * 待办跳转
 *
 * @author YangRunkang(cruise)
 * @date 2020/11/22 23:23
 */
@Api(tags = "Todo")
@RestController
@RequiredArgsConstructor
public class TodoPageJumpController {

    private final TodoAggregateService todoAggregateService;

    @ApiOperation("待办")
    @GetMapping("/todo-list")
    public ModelAndView todoList(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }

        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        String userId = ServletUtils.getUserId();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(TODO_INDEX);
        modelAndView.addObject(todoAggregateService.index(userId, pageNum, pageSize));
        modelAndView.addObject(SeoKey.TITLE, "待办");
        modelAndView.addObject(SeoKey.DESCRIPTION, "待办");
        return modelAndView;
    }


}
