package com.github.shoothzj.config.client.impl.memory;

import com.github.shoothzj.config.client.api.BaseConfig;
import com.github.shoothzj.config.client.api.ConfigListener;
import com.github.shoothzj.config.client.impl.common.BaseCcImpl;
import com.github.shoothzj.config.client.impl.common.module.FieldDescribe;
import com.github.shoothzj.config.client.impl.common.service.FieldDescribeService;
import com.github.shoothzj.config.client.impl.common.util.CcUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author hezhangjian
 */
@Slf4j
public class CcImplMemory extends BaseCcImpl<ConfigDataHolder> {

    @Override
    @SuppressWarnings(value = "unchecked")
    public <T extends BaseConfig> void addConfigVal(T config) {
        configHolderMap.get(CcUtil.getConfigName(config.getClass())).addConfigVal(config);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public <T extends BaseConfig> void modifyConfigVal(T config, int oldVersion) {
        configHolderMap.get(CcUtil.getConfigName(config.getClass())).modifyConfigVal(config, oldVersion);
    }

    @Override
    protected <T extends BaseConfig> void registerConfig(Class<T> configClass, String configName, int version,
                                                         List<FieldDescribe> fieldDescribeList,
                                                         ConfigListener<T> configListener) {
        FieldDescribeService.put(configName, fieldDescribeList);
    }

    @Override
    protected <T extends BaseConfig> void deleteConfigVal(String configName, String configItemId) {
        configHolderMap.get(configName).deleteConfigVal(configItemId);
    }

    @Override
    protected <T extends BaseConfig> void deleteConfigVal(String configName, String configItemId, int version) {
        configHolderMap.get(configName).deleteConfigVal(configItemId, version);
    }

}
