package com.github.shoothzj.config.client.impl.mysql.domain;

import lombok.Data;

/**
 * @author shoothzj
 */
@Data
public class ConfigPo {

    private String configName;

    private String configSchema;

    private int version;

    public ConfigPo() {
    }

}
