package com.github.shoothzj.config.client.impl.memory;

import com.github.shoothzj.config.client.api.BaseConfig;
import com.github.shoothzj.config.client.api.ConfigListener;
import com.github.shoothzj.config.client.impl.common.BaseConfigDataHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author hezhangjian
 */
@Slf4j
public class ConfigDataHolder<T extends BaseConfig> extends BaseConfigDataHolder<T> {

    public ConfigDataHolder(Class<T> configClass, ConfigListener<T> configListener) {
        super(configClass, configListener);
        init();
    }

    @Override
    protected void scheduleSync() {
        // memory, no need to sync
    }

    @Override
    protected void sync(String configItemId) {
        // memory, no need to sync
    }

    @Override
    protected void sync(List<String> needSyncList) {
        // memory, no need to sync
    }

    public void addConfigVal(T config) {
        configDataMap.put(config.getId(), config);
    }

    public void modifyConfigVal(T config, int oldVersion) {
        configDataMap.computeIfPresent(config.getId(), (s, t) -> {
            if (t.getVersion() == oldVersion) {
                return config;
            }
            return t;
        });
    }

    public void deleteConfigVal(String configItemId) {
        configDataMap.remove(configItemId);
    }

    public void deleteConfigVal(String configItemId, int version) {
        configDataMap.computeIfPresent(configItemId, (s, t) -> {
            if (t.getVersion() == version) {
                return null;
            }
            return t;
        });
    }

}
