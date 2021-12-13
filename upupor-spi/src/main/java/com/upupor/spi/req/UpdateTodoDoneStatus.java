package com.upupor.spi.req;

import lombok.Data;

/**
 * 更新todo完成状态
 *
 * @author YangRunkang(cruise)
 * @date 2021/07/22 00:10
 */
@Data
public class UpdateTodoDoneStatus {
    private String todoId;
}
