package com.itdl.utils;

import com.itdl.common.enums.RespCode;
import com.itdl.exception.BizException;
import com.itdl.mapper.MyBaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class PageUtil {
    /**
     *
     * @param mapper 分页查询Mapper
     * @param param 分页查询和统计数量查询条件参数
     * @param pageSize 分页大小，每次查询的数量
     * @param callBack 分页查询回调函数，因为分页数据可能过多，全部放在list会导致程序内存所占用过多 最终导致OOM
     */
    public static  <M extends MyBaseMapper<T>, T, P extends T> void pageQuery(M mapper,
                                                                              P param,
                                                                              Integer pageSize,
                                                                              CustomerPageCallBack<P> callBack){
        pageQuery(mapper, MyBaseMapper.COUNT_METHOD_NAME, MyBaseMapper.PAGE_METHOD_NAME, param, pageSize, callBack);
    }


    /**
     *
     * @param mapper 分页查询Mapper
     * @param countMethodName 自定义统计条数方法
     * @param pageMethodName 自定义分页查询方法
     * @param param 分页查询和统计数量查询条件参数
     * @param pageSize 分页大小，每次查询的数量
     * @param callBack 分页查询回调函数，因为分页数据可能过多，全部放在list会导致程序内存所占用过多 最终导致OOM
     */
    @SuppressWarnings("unchecked")
    public static  <M extends MyBaseMapper<T>, T, P extends T> void pageQuery(M mapper,
                                                                              String countMethodName,
                                                                              String pageMethodName,
                                                                              P param,
                                                                              Integer pageSize,
                                                                              CustomerPageCallBack<P> callBack){

        Method method = Arrays.stream(mapper.getClass().getDeclaredMethods()).filter(s -> StringUtils.equals(countMethodName, s.getName())).findFirst().orElse(null);
        if (method == null){
            throw new BizException(RespCode.REQUEST_PARAM_ERROR.getCode(), "分页查询【获取总记录数】的方法名：" + countMethodName + "不存在或参数错误: ");
        }
        Integer count;
        try {
            count = (Integer) method.invoke(mapper, param);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BizException(RespCode.REQUEST_PARAM_ERROR.getCode(), "分页查询【获取总记录数】的方法" + countMethodName + "执行出错: "+ e.getMessage());
        }

        // 为0或null表示没有记录
        if (count == null || count == 0){
            log.error("====>>>总记录数为空，无需分页查询");
            return;
        }

        Method pageMethod = Arrays.stream(mapper.getClass().getDeclaredMethods()).filter(s -> StringUtils.equals(pageMethodName, s.getName())).findFirst().orElse(null);
        if (pageMethod == null){
            throw new BizException(RespCode.REQUEST_PARAM_ERROR.getCode(), "分页查询【获取分页数据】的方法名：" + countMethodName + "不存在或参数错误: ");
        }

        // 计算能分几页
        int pages = count % pageSize == 0 ? (count / pageSize) : (count / pageSize) + 1;

        // 循环查询
        Long maxId = 0L;
        StopWatch stopWatchTotal = new StopWatch();
        stopWatchTotal.start();
        for (int i = 0; i < pages; i++) {
            // 此时需要注意：获取数量和获取分页结果的条件必须一致，不然会导致maxId不对
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            // 获取查询结果
            List<P> resultList;
            try {
                resultList = (List<P>) pageMethod.invoke(mapper, maxId, pageSize, param);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BizException(RespCode.REQUEST_PARAM_ERROR.getCode(), "分页查询【获取分页数据】的方法" + countMethodName + "执行出错: "+ e.getMessage());
            }

            stopWatch.stop();

            log.info("====>>>自定义分页查询成功：查询到{}条记录，总页数：{}，当前页：{}，耗时：{}s", resultList.size(), pages, i + 1, stopWatch.getTotalTimeSeconds());

            // 执行回调函数
            log.info("====>>>执行回调函数开始");
            stopWatch.start();

            // 获取maxId
            maxId = callBack.getMaxId(resultList);

            // 执行回调逻辑
            callBack.handle(resultList);

            stopWatch.stop();

            // 清空List 释放内存
            resultList.clear();
            log.info("====>>>执行回调函数结束, 耗时：{}s", stopWatch.getTotalTimeSeconds());
        }
        stopWatchTotal.stop();
        log.info("====>>>分页查询整体耗时：{}s", stopWatchTotal.getTotalTimeSeconds());
    }
    /**
     * 自定义分页回调函数
     * @param <T>回调实体泛型
     */
    public interface CustomerPageCallBack<T>{
        /**
         * @Description 功能描述：获取本次查询的最大ID，用于下一次查询的开始位置
         * @Param {@link List<T>} resultList 上一次查询结果列表
         **/
        Long getMaxId(List<T> resultList);


        /**
         * @Description 功能描述：对每一次的分页查询结果进行回调处理
         * @Param {@link List<T>} resultList 结果列表
         **/
        void handle(List<T> resultList);
    }
}
