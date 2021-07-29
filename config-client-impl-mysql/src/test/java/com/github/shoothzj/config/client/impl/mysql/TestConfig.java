package com.github.shoothzj.config.client.impl.mysql;

import com.github.shoothzj.config.client.api.BaseConfig;
import com.github.shoothzj.config.client.api.annotation.ConfClass;
import com.github.shoothzj.config.client.api.annotation.ConfField;

/**
 * @author shoothzj
 */
@ConfClass(version = 1)
public class TestConfig extends BaseConfig {

    @ConfField(name = "name")
    public String name;

    @ConfField(name = "age")
    public int age;

}
