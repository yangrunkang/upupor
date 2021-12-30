package com.upupor.service.business.aggregation;

import com.upupor.service.business.aggregation.service.TodoService;
import com.upupor.service.dto.page.TodoIndexDto;
import com.upupor.service.dto.page.common.ListTodoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author YangRunkang(cruise)
 * @date 2021/07/22 01:19
 */
@Service
@RequiredArgsConstructor
public class TodoAggregateService {

    private final TodoService service;

    public TodoIndexDto index(String userId, Integer pageNum, Integer pageSize) {
        TodoIndexDto todo = new TodoIndexDto();
        ListTodoDto listTodoDto = service.listByUserId(userId, pageNum, pageSize);


        todo.setListTodoDto(listTodoDto);
        return todo;
    }

}