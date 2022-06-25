package com.itdl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MyBaseMapper<T> extends BaseMapper<T> {

    String COUNT_METHOD_NAME = "myCustomCount";
    String PAGE_METHOD_NAME = "myCustomPage";


    /**
     * 通用获取分页总数方法
     * @param param 查询参数条件
     */
    <P extends T> Integer myCustomCount(@Param(Constants.ENTITY) P param);


    /**
     * 通用获取分页方法
     * @param param 查询参数条件
     */
    <P extends T> List<P> myCustomPage(@Param("maxId") Long maxId, @Param("pageSize") Integer pageSize, @Param(Constants.ENTITY) P param);
}
