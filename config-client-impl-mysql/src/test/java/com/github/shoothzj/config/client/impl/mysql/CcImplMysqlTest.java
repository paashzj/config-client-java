package com.github.shoothzj.config.client.impl.mysql;

import com.github.shoothzj.config.client.api.ConfigListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
@Slf4j
public class CcImplMysqlTest {

    @Test
    public void integrateTest() throws InterruptedException {
        CcImplMysql ccImplMysql = new CcImplMysql();
        ccImplMysql.registerConfig(TestConfig.class, new ConfigListener<TestConfig>() {
            @Override
            public void addConfig(TestConfig config) {

            }

            @Override
            public void modifyConfig(TestConfig oldConfig, TestConfig newConfig) {

            }

            @Override
            public void deleteConfig(TestConfig config) {

            }
        });
        {
            TestConfig testConfig = new TestConfig();
            testConfig.setId("id");
            testConfig.setVersion(1);
            ccImplMysql.addConfigVal(testConfig);
        }

        Thread.sleep(3000);
        {
            TestConfig testConfig = ccImplMysql.getConfigVal(TestConfig.class, "id");
            log.info("test config is {}", testConfig);
        }

        {
            TestConfig testConfig = new TestConfig();
            testConfig.setId("id");
            testConfig.setVersion(2);
            testConfig.name = "name";
            testConfig.age = 111;
            ccImplMysql.modifyConfigVal(testConfig, 1);
        }

        {
            ccImplMysql.deleteConfigVal(TestConfig.class, "id", 2);
        }


        Thread.sleep(3000);
    }

}
