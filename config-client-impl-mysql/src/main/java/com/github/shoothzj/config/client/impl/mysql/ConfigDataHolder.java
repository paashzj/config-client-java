package com.github.shoothzj.config.client.impl.mysql;

import com.github.shoothzj.config.client.api.BaseConfig;
import com.github.shoothzj.config.client.api.ConfigListener;
import com.github.shoothzj.config.client.impl.common.BaseConfigDataHolder;
import com.github.shoothzj.config.client.impl.common.module.FieldDescribe;
import com.github.shoothzj.config.client.impl.common.module.FieldType;
import com.github.shoothzj.config.client.impl.common.module.IdVersion;
import com.github.shoothzj.config.client.impl.common.util.CcUtil;
import com.github.shoothzj.config.client.impl.common.util.ReflectionUtil;
import com.github.shoothzj.config.client.impl.mysql.connector.MysqlConnector;
import com.github.shoothzj.config.client.impl.mysql.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shoothzj
 */
@Slf4j
public class ConfigDataHolder<T extends BaseConfig> extends BaseConfigDataHolder<T> {

    private final String tableName;

    private final MysqlConnector mysqlConnector;

    public ConfigDataHolder(Class<T> configClass, ConfigListener<T> configListener, MysqlConnector mysqlConnector) {
        super(configClass, configListener);
        this.tableName = SqlUtil.getTableName(configClass);
        this.mysqlConnector = mysqlConnector;
        init();
    }

    @Override
    protected void scheduleSync() {
        Map<String, IdVersion> auxMap = new HashMap<>();
        try (SqlSession sqlSession = this.mysqlConnector.getSessionFactory().openSession()) {
            Connection connection = sqlSession.getConnection();
            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT id, version FROM " + this.tableName);
            ResultSet executeQuery = preparedStatement.executeQuery();
            while (executeQuery.next()) {
                IdVersion idVersion = new IdVersion();
                String id = executeQuery.getString("id");
                idVersion.setId(id);
                idVersion.setVersion(executeQuery.getInt("version"));
                auxMap.put(id, idVersion);
            }
        } catch (Exception e) {
            log.error("execute sql exception ", e);
        }
        List<String> needSyncList = new ArrayList<>();
        for (IdVersion idVersion : auxMap.values()) {
            T t = configDataMap.get(idVersion.getId());
            if (t == null) {
                needSyncList.add(idVersion.getId());
                continue;
            }
            if (t.getVersion() != idVersion.getVersion()) {
                needSyncList.add(idVersion.getId());
            }
        }
        configDataMap.forEach((s, t) -> {
            if (auxMap.get(s) == null) {
                needSyncList.add(s);
            }
        });
        sync(needSyncList);
    }

    @Override
    protected void sync(List<String> needSyncList) {
        log.info("begin to sync, list is {}", needSyncList);
        if (needSyncList.size() == 0) {
            return;
        }
        Map<String, T> map = new HashMap<>();
        try (SqlSession sqlSession = this.mysqlConnector.getSessionFactory().openSession()) {
            Connection connection = sqlSession.getConnection();
            String sql = "SELECT * FROM " + tableName + " WHERE id in ("
                    + SqlUtil.preparePlaceHolders(needSyncList.size()) + ")";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 1; i <= needSyncList.size(); i++) {
                preparedStatement.setString(i, needSyncList.get(i - 1));
            }
            ResultSet executeQuery = preparedStatement.executeQuery();
            while (executeQuery.next()) {
                T config = convertToConfig(executeQuery);
                if (config != null) {
                    map.put(config.getId(), config);
                }
            }
        } catch (Exception e) {
            log.error("execute sql exception ", e);
        }
        for (T configVal : map.values()) {
            this.configDataMap.compute(configVal.getId(), (s, t) -> {
                if (t == null) {
                    notifyAdd(configVal);
                } else if (configVal.getVersion() > t.getVersion()) {
                    notifyMod(t, configVal);
                }
                return configVal;
            });
        }
        for (String s : needSyncList) {
            if (map.get(s) == null) {
                configDataMap.computeIfPresent(s, (s1, t) -> {
                    notifyDel(t);
                    return null;
                });
            }
        }
    }

    @Override
    public void sync(String configItemId) {
        this.sync(Collections.singletonList(configItemId));
    }

    @SuppressWarnings(value = "deprecated")
    private T convertToConfig(ResultSet executeQuery) {
        try {
            T t = configClass.newInstance();
            t.setId(executeQuery.getString("id"));
            t.setVersion(executeQuery.getInt("version"));
            List<FieldDescribe> fieldDescribeList = CcUtil.getConfigFieldDescribe(configClass);
            for (FieldDescribe fieldDescribe : fieldDescribeList) {
                Field field = ReflectionUtil.findField(configClass, fieldDescribe.getName());
                if (fieldDescribe.getFieldType().equals(FieldType.INT)) {
                    ReflectionUtil.setField(field, t, executeQuery.getInt(fieldDescribe.getPersistentName()));
                } else {
                    ReflectionUtil.setField(field, t, executeQuery.getObject(fieldDescribe.getPersistentName()));
                }
            }
            return t;
        } catch (Exception e) {
            log.error("exception ", e);
        }
        return null;
    }

}
