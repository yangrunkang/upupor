package com.upupor.service.listener.eventbus.comment;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月13日 00:14
 */
public abstract class AbstractComment {
    /**
     * 站内信
     */
    protected abstract void innerMessage();

    /**
     * 邮件
     */
    protected abstract void email();

    /**
     * 获取积分
     */
    protected abstract void getIntegral();
}
