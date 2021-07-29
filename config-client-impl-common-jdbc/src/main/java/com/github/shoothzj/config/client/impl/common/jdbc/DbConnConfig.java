package com.github.shoothzj.config.client.impl.common.jdbc;

import com.github.shoothzj.javatool.util.EnvUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hezhangjian
 */
@Slf4j
public class DbConnConfig {

    public static final String JDBC_FORMAT = "jdbc:mysql://%s/%s?user=%s&password=%s";

    public static final String HOST = EnvUtil.getStringVar("host", "DB_HOST", "localhost");

    public static final String DATABASE = EnvUtil.getStringVar("database", "DATABASE", "ttbb");

    public static final String USER = EnvUtil.getStringVar("dbUser", "DB_USER", "hzj");

    public static final String PASSWORD = EnvUtil.getStringVar("password", "DB_PASSWORD", "Mysql@123");

    public static final String JDBC_URL = String.format(JDBC_FORMAT, HOST, DATABASE, USER, PASSWORD);

}
