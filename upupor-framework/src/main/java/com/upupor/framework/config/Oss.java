package com.upupor.framework.config;

import com.upupor.framework.utils.CcUtils;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.framework.utils.SystemUtil;
import lombok.Data;
import org.springframework.util.StringUtils;

import static com.upupor.framework.utils.CcUtils.checkEnvIsDev;

/**
 * @author cruise
 * @createTime 2022-01-19 11:59
 */
@Data
public class Oss {
    private String bucketName;
    private String fileHost;

    public String getFileHost() {
        if (StringUtils.isEmpty(fileHost) && checkEnvIsDev()) {
            UpuporConfig upuporConfig = SpringContextUtils.getBean(UpuporConfig.class);
            return String.format("http://%s:%s/", SystemUtil.getHostAddress(), upuporConfig.getServerPort());
        }
        return fileHost;
    }
}
