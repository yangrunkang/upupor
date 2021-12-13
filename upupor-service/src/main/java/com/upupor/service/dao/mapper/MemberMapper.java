package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Member;
import com.upupor.spi.req.GetMemberReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberMapper extends BaseMapper<Member> {


    List<Member> selectByEmail(String email);

    List<Member> selectByPhone(String phone);

    Member select(GetMemberReq getMemberReq);



    Integer total();

    Integer totalNormal();

    /**
     * 批量根据用户id获取用户名
     *
     * @param userIdList
     * @return
     */
    List<Member> listByUserIdList(@Param("userIdList") List<String> userIdList);

    /**
     * 查询用户列表
     *
     * @return
     */
    List<Member> list();

    /**
     * 活跃用户
     *
     * @return
     */
    List<Member> activeMember();

    /**
     * 统计不活跃的用户数
     *
     * @return
     */
    Integer countUnActivityMemberList(@Param("currentTime") Long currentTime);


    List<Member> listUnActivityMemberList(long currentTime);
}
