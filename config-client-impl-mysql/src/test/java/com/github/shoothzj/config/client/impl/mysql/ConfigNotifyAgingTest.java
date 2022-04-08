package com.github.shoothzj.config.client.impl.mysql;

import com.github.shoothzj.config.client.impl.mysql.connector.MysqlConnector;
import com.github.shoothzj.config.client.impl.mysql.domain.ConfigNotifyPo;
import com.github.shoothzj.config.client.impl.mysql.mapper.ConfigNotifyMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author hezhangjian
 */
@Disabled
public class ConfigNotifyAgingTest {

    private final MysqlConnector mysqlConnector = new MysqlConnector();

    private final LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

    private final String configName = "configName";

    private final String configItemId = "itemId";

    @Test
    public void configNotifyAging() {
        try (SqlSession sqlSession = mysqlConnector.getSessionFactory().openSession()) {
            ConfigNotifyMapper configNotifyMapper = sqlSession.getMapper(ConfigNotifyMapper.class);
            configNotifyMapper.saveConfig(new ConfigNotifyPo(configName, configItemId, now.minusHours(6)));
            sqlSession.commit();
        }

        try (SqlSession sqlSession = mysqlConnector.getSessionFactory().openSession()) {
            ConfigNotifyMapper configNotifyMapper = sqlSession.getMapper(ConfigNotifyMapper.class);
            configNotifyMapper.deleteBefore(LocalDateTime.now(ZoneId.of("UTC")).minusHours(5));
            sqlSession.commit();
        }
    }

}
