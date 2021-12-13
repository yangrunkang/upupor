package com.upupor.web.controller;

import com.alibaba.fastjson.JSON;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcResponse;
import com.upupor.service.utils.RedisUtil;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.AddCacheContentReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 缓存接口服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/04/19 17:34
 */
@Api(tags = "缓存服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cache")
public class CacheController {

    @ApiOperation("添加缓存")
    @PostMapping(value = "/add")
    @ResponseBody
    public CcResponse cacheContent(AddCacheContentReq addCacheContentReq) {
        CcResponse ccResponse = new CcResponse();

        // 缓存Key contentCache:userId
        String cacheContentKey = CcConstant.CvCache.CONTENT_CACHE_KEY + ServletUtils.getUserId();
        RedisUtil.set(cacheContentKey, JSON.toJSONString(addCacheContentReq));

        return ccResponse;
    }

}
