package com.github.shoothzj.config.client.api;

/**
 * @author shoothzj
 */
public interface ConfigListener<T extends BaseConfig> {

    void addConfig(T config);

    void modifyConfig(T oldConfig, T newConfig);

    void deleteConfig(T config);

}
