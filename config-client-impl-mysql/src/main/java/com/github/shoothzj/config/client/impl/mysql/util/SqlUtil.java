package com.github.shoothzj.config.client.impl.mysql.util;

import com.github.shoothzj.config.client.api.BaseConfig;
import com.github.shoothzj.config.client.impl.common.module.FieldDescribe;
import com.github.shoothzj.config.client.impl.common.util.CcUtil;
import com.github.shoothzj.config.client.impl.common.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author shoothzj
 */
public class SqlUtil {

    public static <T extends BaseConfig> void fillInInsertParam(PreparedStatement preparedStatement, T config) throws SQLException {
        List<Object> list = new ArrayList<>();
        list.add(config.getId());
        fillInParamAux(list, config.getVersion(), config);
        fillInParam(preparedStatement, list);
    }

    public static <T extends BaseConfig> void fillInUpdateParam(PreparedStatement preparedStatement, T config, int oldVersion) throws SQLException {
        List<Object> list = new ArrayList<>();
        fillInParamAux(list, config.getVersion(), config);
        list.add(config.getId());
        list.add(oldVersion);
        fillInParam(preparedStatement, list);
    }

    private static <T extends BaseConfig> void fillInParamAux(List<Object> list, int version, T config) {
        list.add(version);
        List<FieldDescribe> fieldDescribeList = CcUtil.getConfigFieldDescribe(config.getClass());
        for (FieldDescribe fieldDescribe : fieldDescribeList) {
            Field field = ReflectionUtil.findField(config.getClass(), fieldDescribe.getName());
            list.add(ReflectionUtil.getField(field, config));
        }
    }

    private static void fillInParam(PreparedStatement preparedStatement, List<Object> objects) throws SQLException {
        for (int i = 1; i <= objects.size(); i++) {
            Object object = objects.get(i - 1);
            if (object == null) {
                preparedStatement.setObject(i, null);
            } else if (object instanceof Integer) {
                preparedStatement.setInt(i, (Integer) object);
            } else if (object instanceof String) {
                preparedStatement.setString(i, (String) object);
            } else {
                throw new IllegalArgumentException("UnSupport type " + object.getClass());
            }
        }
    }

    public static String preparePlaceHolders(int length) {
        return String.join(",", Collections.nCopies(length, "?"));
    }

    public static String joinByComma(List<String> list) {
        return String.join(",", list);
    }

    public static <T extends BaseConfig> String getDdl(Class<T> configClass) {
        return getDdl(SqlUtil.getTableName(configClass), CcUtil.getConfigFieldDescribe(configClass));
    }

    private static String getDdl(String tableName, List<FieldDescribe> fieldDescribes) {
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + "(\n")
                .append("id VARCHAR(64),\n");
        for (FieldDescribe fieldDescribe : fieldDescribes) {
            if (fieldDescribe.isRequired()) {
                // conn sql
                sql.append(fieldDescribe.getPersistentName()).append(" varchar (255) NOT NULL,\n");
            } else {
                // conn sql
                sql.append(fieldDescribe.getPersistentName()).append(" varchar (255),\n");
            }
        }
        sql.append("version INT,");
        sql.append("primary key (id)");
        sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;");
        return sql.toString();
    }

    public static <T extends BaseConfig> String getTableName(Class<T> configClass) {
        return getTableName(CcUtil.getConfigName(configClass));
    }

    public static String getTableName(String className) {
        return "config_" + className;
    }


}
