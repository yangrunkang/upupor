package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Statement;
import org.apache.ibatis.annotations.Param;

public interface StatementMapper extends BaseMapper<Statement> {

    Statement getByStatementId(@Param("statementId") Integer statementId);

}
