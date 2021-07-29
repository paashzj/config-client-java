package com.github.shoothzj.config.client.impl.common.service;

import com.github.shoothzj.config.client.impl.common.module.FieldDescribe;
import com.github.shoothzj.config.client.impl.common.util.CcUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hezhangjian
 */
@Slf4j
public class FieldDescribeService {

    private static final Map<String, List<FieldDescribe>> fieldDescribeListMap = new ConcurrentHashMap<>();

    public static void put(String key, List<FieldDescribe> fieldDescribeList) {
        fieldDescribeListMap.put(key, fieldDescribeList);
    }

    public static <T> void put(Class<T> configClass, List<FieldDescribe> fieldDescribeList) {
        fieldDescribeListMap.put(CcUtil.getConfigName(configClass), fieldDescribeList);
    }

    public static List<FieldDescribe> get(String key) {
        return fieldDescribeListMap.get(key);
    }

    public static <T> List<FieldDescribe> get(Class<T> configClass) {
        return fieldDescribeListMap.get(CcUtil.getConfigName(configClass));
    }

}
