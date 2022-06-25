package com.itdl.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itdl.entity.UserInfo;
import com.itdl.mapper.UserInfoMapper;
import com.itdl.utils.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserInfoController {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/generatorData")
    public String generatorData(){
        for (int i = 0; i < 1000000; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername("user0" + (i+1));
            userInfo.setPassword("123456");
            userInfo.setMobile("18080000000");
            userInfo.setAge(20);
            userInfo.setSex(i % 2);
            userInfo.setNickname("张小" + (i + 1));
            userInfoMapper.insert(userInfo);
        }

        return "success";
    }

    @GetMapping("/normalQuery")
    public List<UserInfo> normalQuery(){
        // 直接查询所有数据 看看是否会导致OOM
        return userInfoMapper.selectList(new LambdaQueryWrapper<>());
    }


    @GetMapping("/normalPageQuery")
    public String normalPageQuery(){
        // 使用普通的分页查询进行查询
        // 一百万条数据 一次查询一万,循环一千次，然后将一万条数据存储到redis
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 1000; i++) {
            Page<UserInfo> page = new Page<>((i + 1), 1000);
            StopWatch subStopWatch = new StopWatch();
            subStopWatch.start();
            page = userInfoMapper.selectPage(page, new LambdaQueryWrapper<>());
            List<UserInfo> records = page.getRecords();
            handleList(records);
            subStopWatch.stop();
            log.info("=======>>>>普通的分页查询第{}次查询耗时：{}s", (i + 1) ,subStopWatch.getTotalTimeSeconds());
        }

        stopWatch.stop();
        log.info("=======>>>>普通的分页查询总耗时：{}s", stopWatch.getTotalTimeSeconds());
        return "success";
    }


    @GetMapping("/customPageQuery")
    public String customPageQuery(){
        PageUtil.pageQuery(userInfoMapper, new UserInfo(), 1000, new PageUtil.CustomerPageCallBack<>() {
            @Override
            public Long getMaxId(List<UserInfo> resultList) {
                return resultList.stream().mapToLong(UserInfo::getId).max().orElse(0L);
            }

            @Override
            public void handle(List<UserInfo> resultList) {
                handleList(resultList);
            }
        });
        return "success";
    }


    private void handleList(List<UserInfo> records) {
        log.info("======>>>>处理数据进入redis：{}条", records.size());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (UserInfo record : records) {
                connection.set(String.valueOf(record.getId()).getBytes(StandardCharsets.UTF_8),
                        JSONObject.toJSONBytes(record));
            }
            return null;
        });
        stopWatch.stop();
        log.info("========>>>数据入库Redis耗时：{}", stopWatch.getTotalTimeSeconds());
    }
}
