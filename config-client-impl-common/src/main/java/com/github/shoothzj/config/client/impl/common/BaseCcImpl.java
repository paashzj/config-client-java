package com.github.shoothzj.config.client.impl.common;

import com.github.shoothzj.config.client.api.BaseConfig;
import com.github.shoothzj.config.client.api.ConfigListener;
import com.github.shoothzj.config.client.api.ICc;
import com.github.shoothzj.config.client.api.annotation.ConfClass;
import com.github.shoothzj.config.client.impl.common.module.FieldDescribe;
import com.github.shoothzj.config.client.impl.common.util.CcUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 基础实现
 *
 * @author hezhangjian
 */
@Slf4j
public abstract class BaseCcImpl<C extends BaseConfigDataHolder> implements ICc {

    protected final Map<String, C> configHolderMap = new ConcurrentHashMap<>();

    @Override
    public <T extends BaseConfig> void registerConfig(Class<T> configClass, ConfigListener<T> configListener) {
        ConfClass confClass = configClass.getAnnotation(ConfClass.class);
        this.registerConfig(configClass, CcUtil.getConfigName(configClass), confClass.version(), CcUtil.getConfigFieldDescribe(configClass), configListener);
    }

    @Override
    public <T extends BaseConfig> void deleteConfigVal(Class<T> configClass, String configItemId) {
        this.deleteConfigVal(CcUtil.getConfigName(configClass), configItemId);
    }

    @Override
    public <T extends BaseConfig> void deleteConfigVal(Class<T> configClass, String configItemId, int version) {
        this.deleteConfigVal(CcUtil.getConfigName(configClass), configItemId, version);
    }

    @Override
    public <T extends BaseConfig> void iterate(Class<T> configClass, Consumer<T> consumer) {
        this.iterate(configClass, t -> true, consumer);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public <T extends BaseConfig> void iterate(Class<T> configClass, Predicate<String> idFilter, Consumer<T> consumer) {
        configHolderMap.get(CcUtil.getConfigName(configClass)).iterate(idFilter, consumer);
    }

    protected abstract <T extends BaseConfig> void registerConfig(Class<T> configClass, String configName, int version, List<FieldDescribe> fieldDescribeList, ConfigListener<T> configListener);

    protected abstract <T extends BaseConfig> void deleteConfigVal(String configName, String configItemId);

    protected abstract <T extends BaseConfig> void deleteConfigVal(String configName, String configItemId, int version);

    @SuppressWarnings(value = "unchecked")
    @Override
    public <T extends BaseConfig> T getConfigVal(Class<T> configClass, String configItemId) {
        C configDataHolder = configHolderMap.get(CcUtil.getConfigName(configClass));
        return (T) configDataHolder.getConfig(configItemId);
    }

    public void notify(String configName, String configItemId) {
        configHolderMap.get(configName).sync(configItemId);
    }

}
