package com.upupor.service.business.ad;

import com.upupor.service.common.CcConstant;
import com.upupor.service.dao.entity.*;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * 抽象广告
 *
 * @author Yang Runkang (cruise)
 * @date 2021年12月29日 20:44
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractAd<T extends BaseEntity> {

    /**
     * vo列表
     */
    private List<T> voList;

    public AbstractAd(List<T> voList) {
        this.voList = voList;
    }

    public List<T> getVoList() {
        return voList;
    }

    /**
     * 是否已经存在广告
     *
     * @return
     */
    protected abstract Boolean exists();

    /**
     * 插入广告
     *
     * @param adIndex
     */
    protected abstract void insertAd(int adIndex);


    /**
     * 广告
     */
    void ad() {
        // 个人主页文章列表添加广告
        if (!CollectionUtils.isEmpty(voList)) {
            if (!exists()) {
                int adIndex = new Random().nextInt(CcConstant.Page.SIZE);
                int maxIndex = voList.size() - 1;
                if (adIndex <= 2) {
                    adIndex = 3;
                }
                if (adIndex >= maxIndex) {
                    adIndex = maxIndex;
                }

                insertAd(adIndex);
            }
        }
    }


    public static void ad(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Optional<?> first = list.stream().findFirst();
        if (first.isPresent()) {
            Object o = first.get();
            if (o instanceof Content) {
                new ContentAd((List<Content>) list).ad();
            } else if (o instanceof Comment) {
                new CommentAd((List<Comment>) list).ad();
            } else if (o instanceof Member) {
                new MemberAd((List<Member>) list).ad();
            } else if (o instanceof Radio) {
                new RadioAd((List<Radio>) list).ad();
            }
        }
    }

}