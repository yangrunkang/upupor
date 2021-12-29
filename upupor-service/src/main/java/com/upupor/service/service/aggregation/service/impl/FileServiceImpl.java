package com.upupor.service.service.aggregation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.dao.entity.File;
import com.upupor.service.dao.mapper.FileMapper;
import com.upupor.service.service.aggregation.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author YangRunkang(cruise)
 * @date 2020/11/17 22:36
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileMapper fileMapper;

    @Override
    public Boolean addFile(File file) {
        return fileMapper.insert(file) > 0;
    }

    /**
     * 获取用户历史头像
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> getUserHistoryViaList(String userId) {
        return fileMapper.historyVia(userId);
    }

    @Override
    public File selectByMd5(String fileMd5) {
        LambdaQueryWrapper<File> fileQuery = new LambdaQueryWrapper<File>()
                .eq(File::getFileMd5, fileMd5);
        return fileMapper.selectOne(fileQuery);
    }

    @Override
    public File selectByFileUrl(String fileUrl) {
        LambdaQueryWrapper<File> fileQuery = new LambdaQueryWrapper<File>()
                .eq(File::getFileUrl, fileUrl);
        return fileMapper.selectOne(fileQuery);
    }

    @Override
    public void update(File fileByMd5) {
        fileMapper.updateById(fileByMd5);
    }
}
