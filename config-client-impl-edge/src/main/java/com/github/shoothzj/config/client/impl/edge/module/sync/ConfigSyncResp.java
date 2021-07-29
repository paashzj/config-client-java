package com.github.shoothzj.config.client.impl.edge.module.sync;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author hezhangjian
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigSyncResp {

    private List<String> notifyList;


}
