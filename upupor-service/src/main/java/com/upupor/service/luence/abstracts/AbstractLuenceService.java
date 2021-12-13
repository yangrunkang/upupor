package com.upupor.service.luence.abstracts;

import java.util.List;

/**
 * 抽象出Luence全文搜索服务
 *
 * @author YangRunkang(cruise)
 * @date 2021/06/11 22:27
 */
public abstract class AbstractLuenceService<T extends BaseLuenceDto> {

    /**
     * 保存
     *
     * @param t
     */
    public abstract void save(T t);

    /**
     * 删除
     *
     * @param t
     */
    public abstract void delete(T t);

    /**
     * 查询
     *
     * @param t
     */
    public abstract List<T> query(T t);

    /**
     * 更新
     *
     * @param t
     */
    public void update(T t) {
        delete(t);
        save(t);
    }


}
