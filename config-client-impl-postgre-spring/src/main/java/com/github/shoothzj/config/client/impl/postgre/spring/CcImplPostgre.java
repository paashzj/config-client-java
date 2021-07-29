package com.github.shoothzj.config.client.impl.postgre.spring;

import com.github.shoothzj.config.client.api.BaseConfig;
import com.github.shoothzj.config.client.api.ConfigListener;
import com.github.shoothzj.config.client.impl.common.BaseCcImpl;
import com.github.shoothzj.config.client.impl.common.module.FieldDescribe;
import com.github.shoothzj.config.client.impl.common.service.FieldDescribeService;
import com.github.shoothzj.config.client.impl.common.util.CcUtil;
import com.github.shoothzj.config.client.impl.common.util.ReflectionUtil;
import com.github.shoothzj.config.client.impl.postgre.spring.domain.ConfigNotifyPo;
import com.github.shoothzj.config.client.impl.postgre.spring.domain.ConfigPo;
import com.github.shoothzj.config.client.impl.postgre.spring.repository.ConfigNotifyRepository;
import com.github.shoothzj.config.client.impl.postgre.spring.repository.ConfigRepository;
import com.github.shoothzj.config.client.impl.postgre.spring.service.DbInsertService;
import com.github.shoothzj.config.client.impl.postgre.spring.util.SqlUtil;
import com.github.shoothzj.javatool.service.JacksonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hezhangjian
 */
@Slf4j
@Service
public class CcImplPostgre extends BaseCcImpl<ConfigDataHolder> {

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private ConfigNotifyRepository configNotifyRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DbInsertService dbInsertService;

    @Override
    public <T extends BaseConfig> void addConfigVal(T config) {
        //从数据中获取到对应的配置
        final String configName = CcUtil.getConfigName(config.getClass());
        List<FieldDescribe> fieldDescribeList = FieldDescribeService.get(config.getClass());
        String tableName = SqlUtil.getTableName(config.getClass());
        Map<String, Object> parameters = new HashMap<>();
        for (FieldDescribe fieldDescribeDto : fieldDescribeList) {
            Field field = ReflectionUtil.findField(config.getClass(), fieldDescribeDto.getName());
            parameters.put(fieldDescribeDto.getPersistentName(), ReflectionUtil.getField(field, config));
        }
        parameters.put("version", 0);
        final int execute = dbInsertService.get(tableName).execute(parameters);
        if (execute == 1) {
            asyncConfigNotify(configName, config.getId());
        } else {
            throw new IllegalStateException("update config failed");
        }
    }

    @Override
    public <T extends BaseConfig> void modifyConfigVal(T config, int oldVersion) {
        //从数据中获取到对应的配置
        final String configName = CcUtil.getConfigName(config.getClass());
        List<FieldDescribe> fieldDescribeList = FieldDescribeService.get(config.getClass());
        String tableName = SqlUtil.getTableName(config.getClass());
        StringBuilder sqlBuilder = new StringBuilder("UPDATE ").append(tableName).append(" SET");

        List<String> colNameList = new ArrayList<>();
        List<Object> colValueList = new ArrayList<>();
        for (FieldDescribe fieldDescribe : fieldDescribeList) {
            // interact with db, persistent name
            colNameList.add(fieldDescribe.getPersistentName());
            // find in class, name
            Field field = ReflectionUtil.findField(config.getClass(), fieldDescribe.getName());
            colValueList.add(ReflectionUtil.getField(field, config));
        }
        colNameList.add("version");
        colValueList.add(config.getVersion());
        String auxColName = colNameList.stream().map(s -> s + "= ?").collect(Collectors.joining(","));
        sqlBuilder.append(" ").append(auxColName).append(" WHERE id = ? AND version = ?");
        int updateResult = jdbcTemplate.update(sqlBuilder.toString(), SqlUtil.updateSqlHelp(colValueList, config.getId(), oldVersion));
        log.info("updateResult result is [{}]", updateResult);
        if (updateResult == 1) {
            asyncConfigNotify(configName, config.getId());
        } else {
            throw new IllegalStateException("update config failed");
        }
    }

    @Override
    public <T extends BaseConfig> void deleteConfigVal(String configName, String configItemId) {
        String tableName = SqlUtil.getTableName(configName);
        String sqlBuilder = "DELETE FROM " + tableName + " WHERE id = ?";
        int deleteRes = jdbcTemplate.update(sqlBuilder, configItemId);
        log.info("update result is [{}]", deleteRes);
        if (deleteRes == 1) {
            asyncConfigNotify(configName, configItemId);
        }
    }

    @Override
    public <T extends BaseConfig> void deleteConfigVal(String configName, String configItemId, int version) {
        String tableName = SqlUtil.getTableName(configName);
        String sqlBuilder = "DELETE FROM " + tableName + " WHERE id = ? AND version = ?";
        int deleteRes = jdbcTemplate.update(sqlBuilder, configItemId, version);
        log.info("update result is [{}]", deleteRes);
        if (deleteRes == 1) {
            asyncConfigNotify(configName, configItemId);
        }
    }

    @Override
    protected <T extends BaseConfig> void registerConfig(Class<T> configClass, String configName, int version, List<FieldDescribe> fieldDescribeList, ConfigListener<T> configListener) {
        final ConfigPo configPoQuery = configRepository.findByConfigName(configName);
        if (configPoQuery == null) {
            final ConfigPo configPo = new ConfigPo();
            configPo.setConfigName(configName);
            configPo.setVersion(version);
            configPo.setSchema(JacksonService.toJson(fieldDescribeList));
            final ConfigPo saveResult = configRepository.save(configPo);
            // 建表
            final String ddl = SqlUtil.getDdl(configClass);
            try {
                jdbcTemplate.execute(ddl);
                putData(configClass, configListener, configName, fieldDescribeList);
            } catch (Exception e) {
                //create table error, start to rollback
                configRepository.delete(saveResult);
                throw new IllegalStateException("register config failed");
            }
            return;
        }
        // 存在，判断version
        if (configPoQuery.getVersion() >= version) {
            putData(configClass, configListener, configName, fieldDescribeList);
        }
        // todo 兼容性判断
    }

    private <T extends BaseConfig> void putData(Class<T> configClass, ConfigListener<T> configListener, String configName, List<FieldDescribe> fieldDescribeList) {
        configHolderMap.put(configName, new ConfigDataHolder<>(configClass, configListener));
        FieldDescribeService.put(configName, fieldDescribeList);
    }

    private void asyncConfigNotify(String configName, String itemId) {
        CcPool.getAsyncNotifyExecutor().execute(() -> {
            ConfigNotifyPo configNotifyDao = new ConfigNotifyPo();
            configNotifyDao.setConfigName(configName);
            configNotifyDao.setConfigItemId(itemId);
            configNotifyDao.setNotifyTime(LocalDateTime.now(ZoneId.of("UTC")));
            configNotifyRepository.save(configNotifyDao);
        });
    }

}
