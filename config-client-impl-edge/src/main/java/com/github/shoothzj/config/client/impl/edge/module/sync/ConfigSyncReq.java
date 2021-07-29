package com.github.shoothzj.config.client.impl.edge.module.sync;

import com.github.shoothzj.config.client.impl.common.module.IdVersion;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author hezhangjian
 */
@Slf4j
@Data
public class ConfigSyncReq {

    List<IdVersion> syncKeys;

}
