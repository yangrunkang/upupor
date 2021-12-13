package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Todo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YangRunkang(cruise) * @date 2021/07/22 01:17
 */
@Data
public class ListTodoDto extends BaseListDto {
    private List<Todo> todoList;

    public ListTodoDto(PageInfo pageInfo) {
        super(pageInfo);
        this.todoList = new ArrayList<>();
    }

    public ListTodoDto() {
        this.todoList = new ArrayList<>();
    }
}
