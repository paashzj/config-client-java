package com.github.shoothzj.config.client.impl.mysql;

import com.github.shoothzj.config.client.api.BaseConfig;
import com.github.shoothzj.config.client.api.ConfigListener;
import com.github.shoothzj.config.client.impl.common.BaseCcImpl;
import com.github.shoothzj.config.client.impl.common.jdbc.DbConnConfig;
import com.github.shoothzj.config.client.impl.common.module.FieldDescribe;
import com.github.shoothzj.config.client.impl.common.service.FieldDescribeService;
import com.github.shoothzj.config.client.impl.common.util.CcUtil;
import com.github.shoothzj.config.client.impl.mysql.connector.MysqlConnector;
import com.github.shoothzj.config.client.impl.mysql.domain.ConfigNotifyPo;
import com.github.shoothzj.config.client.impl.mysql.domain.ConfigPo;
import com.github.shoothzj.config.client.impl.mysql.service.SqlService;
import com.github.shoothzj.config.client.impl.mysql.util.SqlUtil;
import com.github.shoothzj.distribute.impl.mybatis.MybatisLockImpl;
import com.github.shoothzj.javatool.service.JacksonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * @author shoothzj
 */
@Slf4j
public class CcImplMysql extends BaseCcImpl<ConfigDataHolder> {

    private final MysqlConnector mysqlConnector;

    private final MybatisLockImpl distributeLock;

    private final ConfigNotifyScanner configNotifyScanner;

    public CcImplMysql() {
        this.mysqlConnector = new MysqlConnector();
        distributeLock = new MybatisLockImpl();
        this.configNotifyScanner = new ConfigNotifyScanner(this, distributeLock, mysqlConnector);
    }

    public CcImplMysql(String host, String database, String user, String password) {
        this(String.format(DbConnConfig.JDBC_FORMAT, host, database, user, password));
    }

    public CcImplMysql(String jdbcUrl) {
        this.mysqlConnector = new MysqlConnector(jdbcUrl);
        distributeLock = new MybatisLockImpl(jdbcUrl);
        this.configNotifyScanner = new ConfigNotifyScanner(this, distributeLock, mysqlConnector);
    }

    /**
     * 注册配置，在Mysql中形成数据表
     *
     * @param configClass
     * @param configListener
     * @param <T>
     */
    @Override
    public <T extends BaseConfig> void registerConfig(Class<T> configClass, String configName, int version, List<FieldDescribe> fieldDescribeList, ConfigListener<T> configListener) {
        Optional<ConfigPo> configPoOptional = mysqlConnector.findConfig(configName);
        // 先判断数据表配置中是否存在该配置
        if (!configPoOptional.isPresent()) {
            String ddl = SqlUtil.getDdl(configClass);
            ConfigPo configPo = new ConfigPo();
            configPo.setConfigName(configName);
            configPo.setConfigSchema(JacksonService.toJson(fieldDescribeList));
            configPo.setVersion(1);
            mysqlConnector.saveConfig(configPo);
            try (SqlSession sqlSession = mysqlConnector.getSessionFactory().openSession()) {
                Connection connection = sqlSession.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(ddl);
                preparedStatement.execute();
                putData(configClass, configListener, configName, fieldDescribeList);
            } catch (Exception e) {
                mysqlConnector.deleteConfig(configName);
                log.error("exception is ", e);
                throw new IllegalStateException("register config failed");
            }
            return;
        }
        ConfigPo configPo = configPoOptional.get();
        // 存在，判断version
        if (configPo.getVersion() >= version) {
            putData(configClass, configListener, configName, fieldDescribeList);
        }
        // todo 支持version的变更，判断是否兼容性变更，如果不兼容，则抛出异常，兼容则对数据库进行ALTER TABLE
    }

    private <T extends BaseConfig> void putData(Class<T> configClass, ConfigListener<T> configListener, String configName, List<FieldDescribe> fieldDescribeList) {
        configHolderMap.put(configName, new ConfigDataHolder<>(configClass, configListener, mysqlConnector));
        FieldDescribeService.put(configName, fieldDescribeList);
    }

    @Override
    public <T extends BaseConfig> void addConfigVal(T config) {
        // 插入数据值
        String insertSql = SqlService.getInsert(config.getClass());

        try (SqlSession sqlSession = mysqlConnector.getSessionFactory().openSession()) {
            Connection connection = sqlSession.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
            SqlUtil.fillInInsertParam(preparedStatement, config);
            int executeUpdate = preparedStatement.executeUpdate();
            if (executeUpdate > 0) {
                asyncConfigNotify(CcUtil.getConfigName(config.getClass()), config.getId());
            }
        } catch (Exception e) {
            log.error("exception is ", e);
        }

    }

    @Override
    public <T extends BaseConfig> void modifyConfigVal(T config, int oldVersion) {
        // 更新数据值
        String updateSql = SqlService.getUpdate(config.getClass());
        try (SqlSession sqlSession = mysqlConnector.getSessionFactory().openSession()) {
            Connection connection = sqlSession.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(updateSql);
            SqlUtil.fillInUpdateParam(preparedStatement, config, oldVersion);
            int executeUpdate = preparedStatement.executeUpdate();
            if (executeUpdate > 0) {
                asyncConfigNotify(SqlUtil.getTableName(config.getClass()), config.getId());
            }
        } catch (Exception e) {
            log.error("exception is ", e);
        }
    }

    @Override
    public <T extends BaseConfig> void deleteConfigVal(String configName, String configItemId) {
        // 删除数据值
        String tableName = SqlUtil.getTableName(configName);
        String sqlBuilder = "DELETE FROM " + tableName + " WHERE id = ?";
        try (SqlSession sqlSession = mysqlConnector.getSessionFactory().openSession()) {
            Connection connection = sqlSession.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder);
            preparedStatement.setString(1, configItemId);
            int executeUpdate = preparedStatement.executeUpdate();
            if (executeUpdate > 0) {
                asyncConfigNotify(configName, configItemId);
            }
        } catch (Exception e) {
            log.error("exception is ", e);
        }
    }

    @Override
    public <T extends BaseConfig> void deleteConfigVal(String configName, String configItemId, int version) {
        // 删除数据值
        String tableName = SqlUtil.getTableName(configName);
        String sqlBuilder = "DELETE FROM " + tableName + " WHERE id = ? AND version = ?";
        try (SqlSession sqlSession = mysqlConnector.getSessionFactory().openSession()) {
            Connection connection = sqlSession.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder);
            preparedStatement.setString(1, configItemId);
            preparedStatement.setInt(2, version);
            int executeUpdate = preparedStatement.executeUpdate();
            if (executeUpdate > 0) {
                asyncConfigNotify(configName, configItemId);
            }
        } catch (Exception e) {
            log.error("exception is ", e);
        }
    }

    public void asyncConfigNotify(String configName, String configItemId) {
        CcPool.getAsyncNotifyExecutor().execute(() -> {
            ConfigNotifyPo configNotifyDao = new ConfigNotifyPo();
            configNotifyDao.setConfigName(configName);
            configNotifyDao.setConfigItemId(configItemId);
            configNotifyDao.setNotifyTime(LocalDateTime.now(ZoneId.of("UTC")));
            mysqlConnector.saveConfigNotify(configNotifyDao);
        });
    }

}
