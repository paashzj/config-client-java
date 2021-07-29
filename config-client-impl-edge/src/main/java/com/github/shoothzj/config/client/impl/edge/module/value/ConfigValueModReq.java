package com.github.shoothzj.config.client.impl.edge.module.value;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hezhangjian
 */
@Slf4j
@Data
public class ConfigValueModReq {

    private String id;

    private String content;

    private Integer version;

}
