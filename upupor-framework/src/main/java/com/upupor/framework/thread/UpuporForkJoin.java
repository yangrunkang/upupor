package com.upupor.framework.thread;

import java.util.concurrent.ForkJoinPool;

/**
 * UpuporForkJoin
 *
 * @author Yang Runkang (cruise)
 * @date 1/9/23 15:39
 */
public class UpuporForkJoin {
    static final ForkJoinPool commentListForkJoin = new ForkJoinPool();

    public static ForkJoinPool commentListForkJoin() {
        return commentListForkJoin;
    }

}
