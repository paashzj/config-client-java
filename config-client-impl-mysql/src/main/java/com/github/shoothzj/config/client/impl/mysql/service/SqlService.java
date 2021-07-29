package com.github.shoothzj.config.client.impl.mysql.service;

import com.github.shoothzj.config.client.api.BaseConfig;
import com.github.shoothzj.config.client.impl.common.module.FieldDescribe;
import com.github.shoothzj.config.client.impl.common.service.FieldDescribeService;
import com.github.shoothzj.config.client.impl.mysql.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hezhangjian
 */
@Slf4j
public class SqlService {

    public static <T extends BaseConfig> String getInsert(Class<T> configClass) {
        StringBuilder builder = new StringBuilder("INSERT INTO ").append(SqlUtil.getTableName(configClass)).append(" ");
        List<String> columnNameList = new ArrayList<>();
        columnNameList.add("id");
        columnNameList.add("version");
        List<FieldDescribe> fieldDescribeList = FieldDescribeService.get(configClass);
        for (FieldDescribe fieldDescribe : fieldDescribeList) {
            // concat Sql
            columnNameList.add(fieldDescribe.getPersistentName());
        }
        builder.append("(").append(SqlUtil.joinByComma(columnNameList)).append(")")
                .append(" values ").append("(").append(SqlUtil.preparePlaceHolders(columnNameList.size())).append(");");
        return builder.toString();
    }

    public static <T extends BaseConfig> String getUpdate(Class<T> configClass) {
        StringBuilder builder = new StringBuilder("UPDATE ").append(SqlUtil.getTableName(configClass)).append(" SET ");
        List<String> columnNameList = new ArrayList<>();
        columnNameList.add("version");
        List<FieldDescribe> fieldDescribeList = FieldDescribeService.get(configClass);
        for (FieldDescribe fieldDescribe : fieldDescribeList) {
            // concat Sql
            columnNameList.add(fieldDescribe.getPersistentName());
        }
        String aux = columnNameList.stream().map(s -> s + "= ?").collect(Collectors.joining(","));
        builder.append(aux).append(" WHERE id = ? AND version = ?");
        return builder.toString();
    }


}
