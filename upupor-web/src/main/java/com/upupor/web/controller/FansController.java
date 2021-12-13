package com.upupor.web.controller;

import com.upupor.service.common.CcResponse;
import com.upupor.service.service.FanService;
import com.upupor.spi.req.DelFanReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 关注控制层
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/01 21:15
 */
@Api(tags = "关注服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/fans")
public class FansController {


    private final FanService fanService;

    @ApiOperation("删除粉丝")
    @PostMapping(value = "/del")
    @ResponseBody
    public CcResponse del(DelFanReq delFanReq) {

        Integer updateCount = fanService.delFans(delFanReq);

        CcResponse ccResponse = new CcResponse();
        ccResponse.setData(updateCount > 0);
        return ccResponse;
    }


}
