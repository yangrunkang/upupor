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

package com.upupor.web.controller;

import com.upupor.data.dao.entity.Todo;
import com.upupor.data.dao.entity.TodoDetail;
import com.upupor.data.dao.entity.enhance.Converter;
import com.upupor.data.types.TodoStatus;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcResponse;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.CcUtils;
import com.upupor.framework.utils.ServletUtils;
import com.upupor.security.limiter.UpuporLimit;
import com.upupor.service.base.TodoService;
import com.upupor.service.outer.req.AddTodoReq;
import com.upupor.service.outer.req.UpdateTodoDoneStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Objects;

import static com.upupor.security.limiter.LimitType.CREATE_TODO;


/**
 * @author cruise
 * @createTime 2021-07-20 16:43
 */
@Api(tags = "待办相关服务")
@RestController
@RequiredArgsConstructor
@RequestMapping("/todo")
public class TodoController {

    private final TodoService todoService;

    @ApiOperation("添加待办事项")
    @PostMapping(value = "/add")
    @UpuporLimit(limitType = CREATE_TODO)
    public CcResponse addTodo(AddTodoReq addTodoReq) {
        CcResponse ccResponse = new CcResponse();

        String userId = ServletUtils.getUserId();

        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTodoId(CcUtils.getUuId());
        todo.setTitle(addTodoReq.getTodo());
        todo.setStartTime(0L);
        todo.setEndTime(0L);
        todo.setStatus(TodoStatus.UN_DO);
        todo.setCreateTime(CcDateUtil.getCurrentTime());
        todo.setSysUpdateTime(new Date());

        TodoDetail todoDetail = new TodoDetail();
        todoDetail.setTodoId(todo.getTodoId());
        todoDetail.setCreateTime(CcDateUtil.getCurrentTime());
        todoDetail.setDetail(addTodoReq.getTodoDetail());
        todoDetail.setSysUpdateTime(new Date());
        todoDetail.setDetail(addTodoReq.getTodoDetail());

        Integer result = todoService.addTodo(Converter.todoEnhance(todo, todoDetail));
        ccResponse.setData(result > 0);
        return ccResponse;
    }

    @ApiOperation("更新Todo状态是否完成")
    @PostMapping(value = "/markTodo")
    public CcResponse markTodo(UpdateTodoDoneStatus updateTodoDoneStatus) {
        CcResponse ccResponse = new CcResponse();

        String userId = ServletUtils.getUserId();

        String todoId = updateTodoDoneStatus.getTodoId();

        Todo todo = todoService.getByTodoId(todoId);
        if (Objects.isNull(todo)) {
            throw new BusinessException(ErrorCode.TODO_NOT_EXIST);
        }
        if (!todo.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.TODO_NOT_BELONG_TO_YOU);
        }


        Todo update = new Todo();
        update.setId(todo.getId());

        // 如果todo是已经完成,则更新为未完成
        if (TodoStatus.DONE.equals(todo.getStatus())) {
            update.setStatus(TodoStatus.UN_DO);
        } else if (TodoStatus.UN_DO.equals(todo.getStatus())) {
            // 如果todo是未完成,则更新完成
            update.setStatus(TodoStatus.DONE);
        } else {
            throw new BusinessException(ErrorCode.TODO_STATUS_ERROR);
        }

        Integer result = todoService.update(Converter.todoEnhance(todo));
        ccResponse.setData(result > 0);
        return ccResponse;
    }


    @ApiOperation("更新Todo状态是否完成")
    @PostMapping(value = "/delete")
    public CcResponse delete(UpdateTodoDoneStatus updateTodoDoneStatus) {
        CcResponse ccResponse = new CcResponse();
        String userId = ServletUtils.getUserId();

        String todoId = updateTodoDoneStatus.getTodoId();

        Todo todo = todoService.getByTodoId(todoId);
        if (Objects.isNull(todo)) {
            throw new BusinessException(ErrorCode.TODO_NOT_EXIST);
        }

        if (!todo.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.TODO_NOT_BELONG_TO_YOU);
        }

        if (todo.getStatus().equals(TodoStatus.DELETE)) {
            throw new BusinessException(ErrorCode.TODO_ALREADY_DELETE);
        }


        Todo delete = new Todo();
        delete.setId(todo.getId());
        delete.setStatus(TodoStatus.DELETE);

        Integer result = todoService.update(Converter.todoEnhance(delete));
        ccResponse.setData(result > 0);
        return ccResponse;
    }


}
