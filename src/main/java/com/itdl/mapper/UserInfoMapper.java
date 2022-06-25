package com.itdl.mapper;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.itdl.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itdl
 * @since 2022-06-23
 */
@Mapper
public interface UserInfoMapper extends MyBaseMapper<UserInfo> {

    @Select("select ifnull(count(*), 0) from user_info")
    @Override
    Integer myCustomCount(@Param(Constants.ENTITY) UserInfo param);

    @Select("select * from user_info where id > #{maxId} limit #{pageSize}")
    @Override
    List<UserInfo> myCustomPage(@Param("maxId") Long maxId, @Param("pageSize") Integer pageSize, @Param(Constants.ENTITY) UserInfo param);
}
