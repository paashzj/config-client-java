package com.github.shoothzj.config.client.impl.postgre.spring.util;

import com.github.shoothzj.config.client.api.BaseConfig;
import com.github.shoothzj.config.client.impl.common.module.FieldDescribe;
import com.github.shoothzj.config.client.impl.common.util.CcUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author hezhangjian
 */
@Slf4j
public class SqlUtil {

    public static Object[] updateSqlHelp(List<Object> colValList, Object... ids) {
        Object[] array = new Object[colValList.size() + ids.length];
        int i = 0;
        for (; i < colValList.size(); i++) {
            array[i] = colValList.get(i);
        }
        for (Object id : ids) {
            array[i++] = id;
        }
        return array;
    }

    public static <T extends BaseConfig> String getDdl(Class<T> configClass) {
        final String tableName = SqlUtil.getTableName(configClass);
        final List<FieldDescribe> fieldDescribeList = CcUtil.getConfigFieldDescribe(configClass);
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + "(\n").append("ID SERIAL PRIMARY KEY,\n");
        for (FieldDescribe fieldDescribeDto : fieldDescribeList) {
            if (fieldDescribeDto.isRequired()) {
                // concat sql
                sql.append(fieldDescribeDto.getPersistentName()).append(" varchar (255) NOT NULL,\n");
            } else {
                // concat sql
                sql.append(fieldDescribeDto.getPersistentName()).append(" varchar (255),\n");
            }
        }
        sql.append("Version varchar (255),");
        sql.append("CONSTRAINT unique_version_").append(tableName).append(" UNIQUE (Version),\n");
        sql.append(");");
        return sql.toString();
    }

    public static <T extends BaseConfig> String getTableName(Class<T> configClass) {
        return getTableName(CcUtil.getConfigName(configClass));
    }

    public static <T extends BaseConfig> String getTableName(String className) {
        return "config_" + className;
    }

}
