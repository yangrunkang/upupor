package com.upupor.service.service.aggregation;

import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dto.page.EditorIndexDto;
import com.upupor.service.service.aggregation.service.ContentService;
import com.upupor.service.service.aggregation.service.TagService;
import com.upupor.service.utils.ServletUtils;
import com.upupor.spi.req.GetEditorReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.upupor.service.service.aggregation.CommonAggregateService.getCreateContentInfo;

/**
 * 编辑器页面
 *
 * @author runkangyang (cruise)
 * @date 2020.01.07 00:25
 */
@Service
@RequiredArgsConstructor
public class EditorAggregateService {

    private final TagService tagService;
    private final ContentService contentService;

    /**
     * 编辑器首页
     *
     * @param getEditorReq 从哪里进入编辑器
     * @return 返回EditorIndexDto
     */
    public EditorIndexDto index(GetEditorReq getEditorReq) {
        EditorIndexDto editorIndexDto = new EditorIndexDto();
        editorIndexDto.setContentType(getEditorReq.getContentType());
        editorIndexDto.setTagList(tagService.getTagsByType(getEditorReq.getContentType()));
        editorIndexDto.setCreateTag(getEditorReq.getTag());

        if (Objects.nonNull(getEditorReq.getContentId())) {

            Content content = contentService.getManageContentDetail(getEditorReq.getContentId());

            if (Objects.isNull(content)) {
                throw new BusinessException(ErrorCode.CONTENT_NOT_EXISTS);
            }

            // 校验文章是否是作者本人的
            if (!content.getUserId().equals(ServletUtils.getUserId())) {
                throw new BusinessException(ErrorCode.BAN_EDIT_OTHERS_CONTENT);
            }
            editorIndexDto.setContent(content);
        }


        editorIndexDto.setCreateContentDesc(getCreateContentInfo(getEditorReq.getContentType(), getEditorReq.getTag()));

        return editorIndexDto;
    }
}
