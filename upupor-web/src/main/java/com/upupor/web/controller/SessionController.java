package com.upupor.web.controller;

import com.upupor.service.common.CcResponse;
import com.upupor.service.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * Session控制
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/29 04:10
 */
@Api(tags = "Session")
@RestController
@RequiredArgsConstructor
@RequestMapping("/session")
public class SessionController {

    @ApiOperation("获取当前session")
    @GetMapping("/now")
    @ResponseBody
    public CcResponse currSession() {
        CcResponse cc = new CcResponse();
        cc.setData(ServletUtils.getSession().getId());
        return cc;
    }

}
