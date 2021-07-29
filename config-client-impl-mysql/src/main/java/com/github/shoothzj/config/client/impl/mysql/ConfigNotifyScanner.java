package com.github.shoothzj.config.client.impl.mysql;

import com.github.shoothzj.config.client.impl.mysql.connector.MysqlConnector;
import com.github.shoothzj.config.client.impl.mysql.domain.ConfigNotifyPo;
import com.github.shoothzj.config.client.impl.mysql.mapper.ConfigNotifyMapper;
import com.github.shoothzj.distribute.impl.mybatis.MybatisLockImpl;
import com.github.shoothzj.javatool.executor.ShExecutorCreator;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author shoothzj
 */
@Slf4j
public class ConfigNotifyScanner {

    private static final String NOTIFY_AGING_KEY = "notify_aging";

    private final CcImplMysql ccImplMysql;

    private final MybatisLockImpl distributeLock;

    private final MysqlConnector mysqlConnector;

    private long offset = 0;

    public ConfigNotifyScanner(CcImplMysql ccImplMysql, MybatisLockImpl distributeLock, MysqlConnector mysqlConnector) {
        this.ccImplMysql = ccImplMysql;
        this.distributeLock = distributeLock;
        this.mysqlConnector = mysqlConnector;
        ScheduledExecutorService scannerExecutor = ShExecutorCreator.newScheduleExecutorService(1, "notify_scanner");
        ScheduledExecutorService agingExecutor = ShExecutorCreator.newScheduleExecutorService(1, "notify_aging");
        scannerExecutor.scheduleWithFixedDelay(this::fetchNotify, 0, 500, TimeUnit.MILLISECONDS);
        agingExecutor.scheduleWithFixedDelay(this::notifyAging, 1, 2, TimeUnit.HOURS);
    }

    private void fetchNotify() {
        try {
            if (offset == 0) {
                try (SqlSession sqlSession = mysqlConnector.getSessionFactory().openSession()) {
                    ConfigNotifyMapper configNotifyMapper = sqlSession.getMapper(ConfigNotifyMapper.class);
                    offset = configNotifyMapper.findTopByOrderByIdDesc();
                    sqlSession.commit();
                }
            }
            if (offset != 0) {
                try (SqlSession sqlSession = mysqlConnector.getSessionFactory().openSession()) {
                    ConfigNotifyMapper configNotifyMapper = sqlSession.getMapper(ConfigNotifyMapper.class);
                    List<ConfigNotifyPo> configNotifyPos = configNotifyMapper.findNext500(offset);
                    for (ConfigNotifyPo configNotifyPo : configNotifyPos) {
                        ccImplMysql.notify(configNotifyPo.getConfigName(), configNotifyPo.getConfigItemId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("fetch notify exception ", e);
        }
    }

    private void notifyAging() {
        String lockId = null;
        try {
            lockId = distributeLock.requireLock(NOTIFY_AGING_KEY, TimeUnit.MINUTES, 30);
            try (SqlSession sqlSession = mysqlConnector.getSessionFactory().openSession()) {
                ConfigNotifyMapper configNotifyMapper = sqlSession.getMapper(ConfigNotifyMapper.class);
                configNotifyMapper.deleteBefore(LocalDateTime.now(ZoneId.of("UTC")).minusHours(5));
                sqlSession.commit();
            }
        } catch (Exception e) {
            log.error("execute exception ", e);
            try {
                distributeLock.releaseLock(NOTIFY_AGING_KEY, lockId);
            } catch (Exception ex) {
                log.error("release lock exception ", ex);
            }
        }
    }

}
