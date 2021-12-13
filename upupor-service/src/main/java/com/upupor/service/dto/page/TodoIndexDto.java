package com.upupor.service.dto.page;

import com.upupor.service.dto.page.common.ListTodoDto;
import lombok.Data;


/**
 * @author YangRunkang(cruise)
 * @date 2021/07/22 01:15
 */
@Data
public class TodoIndexDto {

    private ListTodoDto listTodoDto;

    public TodoIndexDto() {
        this.listTodoDto = new ListTodoDto();
    }

}
