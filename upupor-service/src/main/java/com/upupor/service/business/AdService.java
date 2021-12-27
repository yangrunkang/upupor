package com.upupor.service.business;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.CcConstant;
import com.upupor.service.dao.entity.*;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * 广告服务
 *
 * @author Yang Runkang (cruise)
 * @date 2021年12月28日 00:29
 * @email: yangrunkang53@gmail.com
 */

public class AdService {


    /**
     * 用户列表广告
     */
    public static void memberListAd(List<Member> memberList) {
        // 个人主页文章列表添加广告
        if (!CollectionUtils.isEmpty(memberList)) {
            boolean exists = memberList.parallelStream().anyMatch(t -> t.getUserId().equals(CcConstant.GoogleAd.FEED_AD));
            if (!exists) {
                int adIndex = new Random().nextInt(CcConstant.Page.SIZE);
                int maxIndex = memberList.size() - 1;
                if (adIndex <= 2) {
                    adIndex = 3;
                }
                if (adIndex >= maxIndex) {
                    adIndex = maxIndex;
                }
                Member ad = new Member();
                ad.setUserId(CcConstant.GoogleAd.FEED_AD);
                memberList.add(adIndex, ad);
            }
        }
    }

    public static void contentListAd(List<Content> contentList) {
        if (!CollectionUtils.isEmpty(contentList)) {
            boolean exists = contentList.parallelStream().anyMatch(t -> t.getContentId().equals(CcConstant.GoogleAd.FEED_AD));
            if (!exists) {
                int adIndex = new Random().nextInt(CcConstant.Page.SIZE);
                int maxIndex = contentList.size() - 1;
                if (adIndex <= 2) {
                    adIndex = 3;
                }
                if (adIndex >= maxIndex) {
                    adIndex = maxIndex;
                }
                Content adContent = new Content();
                adContent.setContentId(CcConstant.GoogleAd.FEED_AD);
                contentList.add(adIndex, adContent);
            }
        }

    }

    public static void radioListAd(List<Radio> radioList) {
        // 个人主页电台列表添加广告
        if (!CollectionUtils.isEmpty(radioList)) {
            boolean exists = radioList.parallelStream().anyMatch(t -> t.getRadioId().equals(CcConstant.GoogleAd.FEED_AD));
            if (!exists) {
                int adIndex = new Random().nextInt(CcConstant.Page.SIZE);
                int maxIndex = radioList.size() - 1;
                if (adIndex <= 2) {
                    adIndex = 3;
                }
                if (adIndex >= maxIndex) {
                    adIndex = maxIndex;
                }
                Radio radio = new Radio();
                radio.setRadioId(CcConstant.GoogleAd.FEED_AD);
                radio.setUserId(CcConstant.GoogleAd.FEED_AD);
                radio.setCreateTime(CcDateUtil.getCurrentTime());
                radioList.add(adIndex, radio);
            }
        }
    }


    public static void commentListAd(List<Comment> commentList) {
        // 个人主页电台列表添加广告
        if (!CollectionUtils.isEmpty(commentList)) {
            boolean exists = commentList.parallelStream().anyMatch(t -> t.getCommentId().equals(CcConstant.GoogleAd.FEED_AD));
            if (!exists) {
                int adIndex = new Random().nextInt(CcConstant.Page.SIZE);
                int maxIndex = commentList.size() - 1;
                if (adIndex <= 2) {
                    adIndex = 3;
                }
                if (adIndex >= maxIndex) {
                    adIndex = maxIndex;
                }
                Comment ad = new Comment();
                ad.setCommentId(CcConstant.GoogleAd.FEED_AD);
                commentList.add(adIndex, ad);
            }
        }
    }


    public static void todoListAd(List<Todo> todoList) {
        // 个人主页电台列表添加广告
        if (!CollectionUtils.isEmpty(todoList)) {
            boolean exists = todoList.parallelStream().anyMatch(t -> t.getTodoId().equals(CcConstant.GoogleAd.FEED_AD));
            if (!exists) {
                int adIndex = new Random().nextInt(CcConstant.Page.SIZE);
                int maxIndex = todoList.size() - 1;
                if (adIndex <= 2) {
                    adIndex = 3;
                }
                if (adIndex >= maxIndex) {
                    adIndex = maxIndex;
                }
                Todo ad = new Todo();
                ad.setTodoId(CcConstant.GoogleAd.FEED_AD);
                todoList.add(adIndex, ad);
            }
        }
    }


}
