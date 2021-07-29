package com.github.shoothzj.config.client.impl.edge.module.batch;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author hezhangjian
 */
@Slf4j
@Data
public class ConfigBatchQueryReq {

    private List<String> idList;

}
