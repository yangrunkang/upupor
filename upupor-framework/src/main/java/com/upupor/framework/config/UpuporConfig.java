/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.framework.config;

import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.config.enums.OssSource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月12日 22:37
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "upupor")
public class UpuporConfig {
    private Pool pool;
    private Thumbnails thumbnails;
    private Email email;
    private String website;
    private Integer adSwitch;
    private String adSwitchRight;
    private String analyzeSwitch;
    // oss资源
    private OssSource ossSource;
    private Minio minio;
    // 静态文件地址
    private String businessStaticSource;
    private String env;
    /**
     * 不用校验是否登录的url
     */
    private List<String> interfaceWhiteUrlList;
    /**
     * 页面需要校验登录的url
     */
    private List<String> pageCheckUrlList;
    private GoogleAd googleAd;

    private String googleTagId;
    private String googleGa4;

    @Value("${server.port}")
    private Integer serverPort;

    /**
     * 获取Oss服务前缀
     * @return
     */
    public String getOssServerPrefix(){
        if(ossSource.equals(OssSource.ALI_OSS)){
            throw new BusinessException(ErrorCode.OSS_UN_IMP);
        }

        if(ossSource.equals(OssSource.MINIO_OSS)){
            // 在nginx做了反向代理  /minio_upupor --> http://ip:port/upupor-img/
            return getMinio().getRequestUrl()  + CcConstant.BACKSLASH;
        }
        throw new BusinessException(ErrorCode.UNKNOWN_EXCEPTION,"系统未适配来源");
    }

}
