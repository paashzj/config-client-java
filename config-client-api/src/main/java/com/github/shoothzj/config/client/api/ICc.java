package com.github.shoothzj.config.client.api;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author shoothzj
 */
public interface ICc {

    /**
     * register config
     * @param configClass 配置类
     * @param configListener
     * @param <T>
     */
    <T extends BaseConfig> void registerConfig(Class<T> configClass, ConfigListener<T> configListener);

    /**
     * add a config value
     * @param config
     * @param <T>
     */
    <T extends BaseConfig> void addConfigVal(T config);

    /**
     * modify a config value based on version
     * @param config
     * @param oldVersion
     * @param <T>
     */
    <T extends BaseConfig> void modifyConfigVal(T config, int oldVersion);

    /**
     * get a config value
     * @param configClass
     * @param configItemId
     * @param <T>
     * @return
     */
    <T extends BaseConfig> T getConfigVal(Class<T> configClass, String configItemId);

    /**
     * delete config val by item id
     * @param configClass
     * @param configItemId
     * @param <T>
     */
    <T extends BaseConfig> void deleteConfigVal(Class<T> configClass, String configItemId);

    /**
     * delete config val by item id and version
     * @param configClass
     * @param configItemId
     * @param version
     * @param <T>
     */
    <T extends BaseConfig> void deleteConfigVal(Class<T> configClass, String configItemId, int version);

    /**
     * 遍历配置中的所有值
     * @param configClass
     * @param consumer
     * @param <T>
     */
    <T extends BaseConfig> void iterate(Class<T> configClass, Consumer<T> consumer);

    /**
     * 遍历配置中的所有值，根据IdFilter来提高性能
     * @param configClass
     * @param idFilter
     * @param consumer
     * @param <T>
     */
    <T extends BaseConfig> void iterate(Class<T> configClass, Predicate<String> idFilter, Consumer<T> consumer);

}
