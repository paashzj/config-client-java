package com.github.shoothzj.config.client.impl.postgre.spring;

import com.github.shoothzj.config.client.api.BaseConfig;
import com.github.shoothzj.config.client.api.ConfigListener;
import com.github.shoothzj.config.client.impl.common.BaseConfigDataHolder;
import com.github.shoothzj.config.client.impl.common.module.IdVersion;
import com.github.shoothzj.config.client.impl.postgre.spring.util.SqlUtil;
import com.github.shoothzj.sdk.spring.util.SpringContextHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hezhangjian
 */
@Slf4j
public class ConfigDataHolder<T extends BaseConfig> extends BaseConfigDataHolder<T> {

    private final String tableName;

    private final JdbcTemplate jdbcTemplate;

    public ConfigDataHolder(Class<T> configClass, ConfigListener<T> configListener) {
        super(configClass, configListener);
        this.tableName = SqlUtil.getTableName(configClass);
        this.jdbcTemplate = SpringContextHelper.getBean(JdbcTemplate.class);
        init();
    }

    @Override
    protected void scheduleSync() {
        Map<String, IdVersion> auxMap = new HashMap<>();
        String sql = "SELECT id, version FROM " + tableName + ";";
        jdbcTemplate.query(sql, rs -> {
            auxMap.put(rs.getString("id"), new IdVersion(rs.getString("id"), rs.getInt("version")));
        });
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
    protected void sync(String configItemId) {
        this.sync(Collections.singletonList(configItemId));
    }

    @Override
    protected void sync(List<String> needSyncList) {
        log.info("begin to sync, list is {}", needSyncList);
        // todo
    }
}
