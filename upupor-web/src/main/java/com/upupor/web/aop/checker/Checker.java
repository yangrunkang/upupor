package com.upupor.web.aop.checker;

/**
 * 检查接口
 * @author cruise
 * @createTime 2022-01-20 18:01
 */
public interface Checker<T extends BaseChecker> {
    void check(T t);
}
