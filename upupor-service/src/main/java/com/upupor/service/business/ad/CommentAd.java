package com.upupor.service.business.ad;

import com.upupor.service.common.CcConstant;
import com.upupor.service.dao.entity.Comment;

import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月29日 20:53
 * @email: yangrunkang53@gmail.com
 */
public class CommentAd extends AbstractAd<Comment> {
    public CommentAd(List<Comment> comments) {
        super(comments);
    }

    @Override
    protected Boolean exists() {
        return getVoList().parallelStream().anyMatch(t -> t.getCommentId().equals(CcConstant.GoogleAd.FEED_AD));
    }

    @Override
    protected void insertAd(int adIndex) {
        Comment ad = new Comment();
        ad.setCommentId(CcConstant.GoogleAd.FEED_AD);
        getVoList().add(adIndex, ad);
    }
}
