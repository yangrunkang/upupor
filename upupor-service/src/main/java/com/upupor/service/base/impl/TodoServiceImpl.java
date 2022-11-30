/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.data.dao.entity.Todo;
import com.upupor.data.dao.entity.TodoDetail;
import com.upupor.data.dao.entity.converter.Converter;
import com.upupor.data.dao.entity.enhance.TodoEnhance;
import com.upupor.data.dao.mapper.TodoDetailMapper;
import com.upupor.data.dao.mapper.TodoMapper;
import com.upupor.data.dto.page.common.ListTodoDto;
import com.upupor.data.types.TodoStatus;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.service.base.TodoService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * TodoService实现类
 *
 * @author YangRunkang(cruise)
 * @date 2021/07/20 00:03
 */
@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;

    private final TodoDetailMapper todoDetailMapper;

    /**
     * 添加todo
     *
     * @param todo
     */
    @Override
    public Integer addTodo(TodoEnhance todoEnhance) {
        int addTodo = todoMapper.insert(todoEnhance.getTodo());
        int addTodoDetail = todoDetailMapper.insert(todoEnhance.getTodoDetail());
        return addTodo + addTodoDetail;
    }

    /**
     * 更新
     *
     * @param todo
     */
    @Override
    public Integer update(TodoEnhance todoEnhance) {
        Todo todo = todoEnhance.getTodo();
        TodoDetail todoDetail = todoEnhance.getTodoDetail();
        if (Objects.isNull(todo) || Objects.isNull(todo.getId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        int update = 0;
        update = update + todoMapper.updateById(todo);
        if (Objects.nonNull(todoDetail)) {
            update = update + todoDetailMapper.updateById(todoDetail);
        }
        return update;
    }

    /**
     * 根据UserId获取
     *
     * @param userId
     * @return
     */
    @Override
    public ListTodoDto listByUserId(String userId, Integer pageNum, Integer pageSize) {

        LambdaQueryWrapper<Todo> todoLambdaQueryWrapper = new LambdaQueryWrapper<Todo>()
                .eq(Todo::getUserId, userId)
                .notIn(Todo::getStatus, TodoStatus.DELETE)
                .orderByDesc(Todo::getCreateTime);

        PageHelper.startPage(pageNum, pageSize);
        List<Todo> todoList = todoMapper.selectList(todoLambdaQueryWrapper);
        PageInfo<Todo> pageInfo = new PageInfo<>(todoList);

        List<TodoEnhance> todoEnhanceList = Converter.todoEnhanceList(todoList);

        ListTodoDto listTodoDto = new ListTodoDto(pageInfo);
        listTodoDto.setTodoEnhanceList(todoEnhanceList);
        return listTodoDto;
    }

    /**
     * 根据用户Id获取
     *
     * @param todoId
     * @return
     */
    @Override
    public Todo getByTodoId(String todoId) {
        if (StringUtils.isEmpty(todoId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        LambdaQueryWrapper<Todo> query = new LambdaQueryWrapper<Todo>()
                .eq(Todo::getTodoId, todoId);

        TodoDetail queryDetail = new TodoDetail();
        queryDetail.setTodoId(todoId);

        Todo todo = todoMapper.selectOne(query);
        if (Objects.isNull(todo)) {
            throw new BusinessException(ErrorCode.TODO_NOT_EXIST);
        }

        return todo;
    }
}
