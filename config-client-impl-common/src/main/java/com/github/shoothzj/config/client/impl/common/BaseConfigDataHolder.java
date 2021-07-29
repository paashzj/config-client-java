package com.github.shoothzj.config.client.impl.common;

import com.github.shoothzj.config.client.api.BaseConfig;
import com.github.shoothzj.config.client.api.ConfigListener;
import com.github.shoothzj.javatool.executor.ShExecutorCreator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author shoothzj
 */
public abstract class BaseConfigDataHolder<T extends BaseConfig> {

    private final ScheduledExecutorService scheduledExecutorService;

    protected final Map<String, T> configDataMap;

    protected final Class<T> configClass;

    protected final ConfigListener<T> configListener;

    public BaseConfigDataHolder(Class<T> configClass, ConfigListener<T> configListener) {
        this.scheduledExecutorService = ShExecutorCreator.newScheduleExecutorService(1, "scheduler-sync");
        this.configDataMap = new ConcurrentHashMap<>();
        this.configClass = configClass;
        this.configListener = configListener;
    }

    public void init() {
        scheduledExecutorService.scheduleWithFixedDelay(this::scheduleSync, 0, 5, TimeUnit.MINUTES);
    }

    public T getConfig(String configItemId) {
        return configDataMap.get(configItemId);
    }

    protected void notifyAdd(T config) {
        this.configListener.addConfig(config);
    }

    protected void notifyMod(T t, T config) {
        this.configListener.modifyConfig(t, config);
    }

    protected void notifyDel(T config) {
        configListener.deleteConfig(config);
    }

    /**
     * 定期同步
     */
    protected abstract void scheduleSync();

    /**
     * 指定单个Id同步
     * @param configItemId
     */
    protected abstract void sync(String configItemId);

    /**
     * 批量同步
     * @param needSyncList
     */
    protected abstract void sync(List<String> needSyncList);

    public void iterate(Predicate<String> idFilter, Consumer<T> consumer) {
        for (Map.Entry<String, T> entry : configDataMap.entrySet()) {
            if (idFilter.test(entry.getKey())) {
                consumer.accept(entry.getValue());
            }
        }
    }

}
