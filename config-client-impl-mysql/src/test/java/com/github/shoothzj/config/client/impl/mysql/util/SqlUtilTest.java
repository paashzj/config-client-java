package com.github.shoothzj.config.client.impl.mysql.util;

import com.github.shoothzj.config.client.impl.mysql.TestConfig;
import org.junit.Test;

public class SqlUtilTest {

    @Test
    public void ddlTest() {
        String ddl = SqlUtil.getDdl(TestConfig.class);
        System.out.println(ddl);
    }

}