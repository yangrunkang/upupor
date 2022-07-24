/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.web.controller;

/**
 * 合并数据
 *
 * @author Yang Runkang (cruise)
 * @createTime 2022-07-23 23:21
 * @email: yangrunkang53@gmail.com
 */
//@RestController
//@RequiredArgsConstructor
//public class MergeDataController {
//
//    private final ContentMapper contentMapper;
//
//    private final DraftMapper draftMapper;
//
//    private final ContentExtendMapper contentExtendMapper;
//
//    @GetMapping("/to-draft")
//    @ResponseBody
//    public CcResponse contentList() {
//        LambdaQueryWrapper<Content> query = new LambdaQueryWrapper<Content>()
//                .eq(Content::getStatus, ContentStatus.DRAFT);
//        List<Content> contentList = contentMapper.selectList(query);
//        if (CollectionUtils.isEmpty(contentList)) {
//            return new CcResponse("草稿状态的内容已清空");
//        }
//
//        List<String> contentIdList = contentList.stream().map(Content::getContentId).distinct().collect(Collectors.toList());
//
//        LambdaQueryWrapper<ContentExtend> queryExtend = new LambdaQueryWrapper<ContentExtend>()
//                .in(ContentExtend::getContentId, contentIdList);
//        List<ContentExtend> contentExtends = contentExtendMapper.selectList(queryExtend);
//
//        for (Content content : contentList) {
//            for (ContentExtend contentExtend : contentExtends) {
//                if (content.getContentId().equals(contentExtend.getContentId())) {
//                    content.setContentExtend(contentExtend);
//                    break;
//                }
//            }
//        }
//
//
//        for (Content content : contentList) {
//            DraftDto draftDto = new DraftDto();
//            draftDto.setTitle(content.getTitle());
//            draftDto.setContent(content.getContentExtend().getDetailContent());
//            draftDto.setMdContent(content.getContentExtend().getMarkdownContent());
//            draftDto.setNoneOriginLink(content.getNoneOriginLink());
//            draftDto.setOriginType(content.getOriginType());
//            draftDto.setContentType(content.getContentType());
//            draftDto.setTagIds(content.getTagIds());
//            draftDto.setPicture(null);
//            draftDto.setPreContentId(content.getContentId());
//            String s = JSONObject.toJSONString(draftDto);
//
//
//            Draft draft = new Draft();
//            draft.setUserId(content.getUserId());
//            draft.setDraftId(content.getContentId());
//            draft.setTitle(content.getTitle());
//            draft.setDraftContent(s);
//            draft.setDraftSource(DraftSource.CONTENT);
//            draft.setCreateTime(content.getCreateTime());
//            draft.setSysUpdateTime(content.getSysUpdateTime());
//            draftMapper.insert(draft);
//            // 删除文章
//            contentMapper.deleteById(content.getId());
//
//        }
//
//        return new CcResponse();
//
//    }
//
//    @Data
//    private class DraftDto {
//        private String title;
//        private String content;
//        private String mdContent;
//        private String noneOriginLink;
//        private OriginType originType;
//        private ContentType contentType;
//        private String tagIds;
//        private String picture;
//        private String preContentId;
//        private String userId;
//    }
//
//}
