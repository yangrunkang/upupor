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

package com.upupor.service.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.data.dao.entity.File;
import com.upupor.service.data.dao.mapper.FileMapper;
import com.upupor.service.data.service.FileService;
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
