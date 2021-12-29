package com.upupor.service.business.aggregation.service;

import com.upupor.service.dao.entity.File;

import java.util.List;

/**
 * @author YangRunkang(cruise)
 * @date 2020/11/17 22:35
 */
public interface FileService {

    /**
     * 添加文件
     *
     * @param file
     * @return
     */
    Boolean addFile(File file);

    /**
     * 获取用户历史头像
     *
     * @param userId
     * @return
     */
    List<String> getUserHistoryViaList(String userId);

    File selectByMd5(String md5);

    File selectByFileUrl(String fileUrl);


    void update(File fileByMd5);
}
