package com.github.shoothzj.config.client.impl.mysql.connector;

import com.github.shoothzj.config.client.impl.common.jdbc.DbConnConfig;
import com.github.shoothzj.config.client.impl.mysql.domain.ConfigNotifyPo;
import com.github.shoothzj.config.client.impl.mysql.domain.ConfigPo;
import com.github.shoothzj.config.client.impl.mysql.mapper.ConfigMapper;
import com.github.shoothzj.config.client.impl.mysql.mapper.ConfigNotifyMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.util.Optional;

/**
 * @author shoothzj
 */
public class MysqlConnector {

    private final HikariConfig config = new HikariConfig();

    private final HikariDataSource ds;

    private final SqlSessionFactory sessionFactory;

    public MysqlConnector() {
        config.setJdbcUrl(DbConnConfig.JDBC_URL);
        ds = new HikariDataSource(config);
        SqlSessionFactoryBuilder sessionFactoryBuilder = new SqlSessionFactoryBuilder();
        Environment environment = new Environment("production", new JdbcTransactionFactory(), ds);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(ConfigMapper.class);
        configuration.addMapper(ConfigNotifyMapper.class);
        sessionFactory = sessionFactoryBuilder.build(configuration);
    }

    public MysqlConnector(String jdbcUrl) {
        config.setJdbcUrl(jdbcUrl);
        ds = new HikariDataSource(config);
        SqlSessionFactoryBuilder sessionFactoryBuilder = new SqlSessionFactoryBuilder();
        Environment environment = new Environment("production", new JdbcTransactionFactory(), ds);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(ConfigMapper.class);
        configuration.addMapper(ConfigNotifyMapper.class);
        sessionFactory = sessionFactoryBuilder.build(configuration);
    }

    public void saveConfig(ConfigPo configPo) {
        try (SqlSession sqlSession = sessionFactory.openSession()) {
            ConfigMapper configMapper = sqlSession.getMapper(ConfigMapper.class);
            configMapper.saveConfig(configPo);
            sqlSession.commit();
        }
    }

    public boolean configExist(String configName) {
        try (SqlSession sqlSession = sessionFactory.openSession()) {
            ConfigMapper configMapper = sqlSession.getMapper(ConfigMapper.class);
            return configMapper.selectConfigPo(configName) != null;
        }
    }

    public Optional<ConfigPo> findConfig(String configName) {
        try (SqlSession sqlSession = sessionFactory.openSession()) {
            ConfigMapper configMapper = sqlSession.getMapper(ConfigMapper.class);
            return Optional.ofNullable(configMapper.selectConfigPo(configName));
        }
    }

    public void deleteConfig(String configName) {
        try (SqlSession sqlSession = sessionFactory.openSession()) {
            ConfigMapper configMapper = sqlSession.getMapper(ConfigMapper.class);
            configMapper.deleteConfigPo(configName);
            sqlSession.commit();
        }
    }

    public void saveConfigNotify(ConfigNotifyPo configNotifyPo) {
        try (SqlSession sqlSession = sessionFactory.openSession()) {
            ConfigNotifyMapper configNotifyMapper = sqlSession.getMapper(ConfigNotifyMapper.class);
            configNotifyMapper.saveConfig(configNotifyPo);
            sqlSession.commit();
        }
    }

    public SqlSessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
