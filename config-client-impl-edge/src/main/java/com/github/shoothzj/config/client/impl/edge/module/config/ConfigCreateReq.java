package com.github.shoothzj.config.client.impl.edge.module.config;

import com.github.shoothzj.config.client.impl.common.module.FieldDescribe;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author hezhangjian
 */
@Slf4j
@Data
public class ConfigCreateReq {

    private String configName;

    private List<FieldDescribe> fields;

}
