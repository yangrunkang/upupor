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

package com.upupor.service.business.comment.list;

import com.upupor.data.dao.entity.Comment;
import com.upupor.data.dao.entity.Radio;
import com.upupor.data.dao.entity.enhance.CommentEnhance;
import com.upupor.data.dto.page.comment.CommentDto;
import com.upupor.data.types.ContentType;
import com.upupor.service.base.RadioService;
import com.upupor.service.business.comment.list.abstracts.AbstractCommentList;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Yang Runkang (cruise)
 * @date 2023年01月09日 15:01
 * @email: yangrunkang53@gmail.com
 */
@Component
public class RadioCommentList extends AbstractCommentList<Radio> {
    @Resource
    private RadioService radioService;

    @Override
    protected Boolean isFit(ContentType commentSource) {
        return ContentType.isRadio(commentSource);
    }

    @Override
    protected List<String> filterTargetIdList() {
        return commentEnhanceList.stream()
                .map(CommentEnhance::getComment)
                .filter(s -> ContentType.isRadio(s.getCommentSource()))
                .map(Comment::getTargetId)
                .collect(Collectors.toList());
    }

    @Override
    protected Map<String, Radio> filteredIdConvertToTMap(List<String> filteredIdList) {
        Map<String, Radio> radioMap = new HashMap<>();
        List<Radio> radios = radioService.listByRadioIdList(filteredIdList);
        radios.forEach(s -> radioMap.putIfAbsent(s.getRadioId(), s));
        return radioMap;
    }

    @Override
    protected void createCommentDto(Map<String, Radio> radioMap, CommentEnhance commentEnhance) {
        Comment comment = commentEnhance.getComment();
        String targetId = comment.getTargetId();

        Radio radio = radioMap.get(targetId);
        if (Objects.isNull(radio)) {
            return;
        }
        commentDtoList.add(CommentDto.create(comment.getCommentContent(),
                "/r/" + radio.getRadioId(),
                radio.getRadioIntro(), commentEnhance
        ));
    }
}
