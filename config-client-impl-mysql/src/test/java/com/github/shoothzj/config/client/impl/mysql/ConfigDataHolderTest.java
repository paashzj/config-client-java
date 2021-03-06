package com.github.shoothzj.config.client.impl.mysql;

import com.github.shoothzj.config.client.api.ConfigListener;
import com.github.shoothzj.config.client.impl.mysql.connector.MysqlConnector;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class ConfigDataHolderTest {

    @Test
    public void testConfigDataHolder() throws InterruptedException {
        ConfigDataHolder<TestConfig> configDataHolder = new ConfigDataHolder<>(TestConfig.class,
                new ConfigListener<TestConfig>() {
            @Override
            public void addConfig(TestConfig config) {

            }

            @Override
            public void modifyConfig(TestConfig oldConfig, TestConfig newConfig) {

            }

            @Override
            public void deleteConfig(TestConfig config) {

            }
        }, new MysqlConnector());
        Thread.sleep(300000);
    }

}
