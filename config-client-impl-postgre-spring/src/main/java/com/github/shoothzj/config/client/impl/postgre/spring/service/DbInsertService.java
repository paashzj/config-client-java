package com.github.shoothzj.config.client.impl.postgre.spring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * @author hezhangjian
 */
@Slf4j
@Service
public class DbInsertService {

    private final DataSource dataSource;

    public DbInsertService(@Autowired DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @param tableName
     * @return
     */
    public SimpleJdbcInsert get(String tableName) {
        return new SimpleJdbcInsert(dataSource).withTableName(tableName);
    }

}
