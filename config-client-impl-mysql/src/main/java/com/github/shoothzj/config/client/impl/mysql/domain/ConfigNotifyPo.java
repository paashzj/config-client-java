package com.github.shoothzj.config.client.impl.mysql.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author shoothzj
 */
@Data
public class ConfigNotifyPo {

    private Long id;

    private String configName;

    private String configItemId;

    private LocalDateTime notifyTime;

    public ConfigNotifyPo() {
    }

    public ConfigNotifyPo(String configName, String configItemId, LocalDateTime notifyTime) {
        this.configName = configName;
        this.configItemId = configItemId;
        this.notifyTime = notifyTime;
    }
}
