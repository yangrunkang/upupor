package com.upupor.service.business.aggregation.service;

import com.upupor.service.dao.entity.Todo;
import com.upupor.service.dto.page.common.ListTodoDto;

/**
 * @author YangRunkang(cruise)
 * @date 2021/07/18 23:53
 */
public interface TodoService {
    /**
     * 添加todo
     */
    Integer addTodo(Todo todo);

    /**
     * 更新
     */
    Integer update(Todo todo);

    /**
     * 根据UserId获取
     *
     * @param userId
     * @return
     */
    ListTodoDto listByUserId(String userId, Integer pageNum, Integer pageSize);

    /**
     * 根据用户Id获取
     *
     * @param todoId
     * @return
     */
    Todo getByTodoId(String todoId);
}
