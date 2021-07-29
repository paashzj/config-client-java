package com.github.shoothzj.config.client.impl.mysql;

import com.github.shoothzj.config.client.impl.mysql.connector.MysqlConnector;
import com.github.shoothzj.config.client.impl.mysql.domain.ConfigNotifyPo;
import com.github.shoothzj.config.client.impl.mysql.mapper.ConfigNotifyMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

/**
 * @author shoothzj
 */
@Slf4j
public class CcNotifyMapperTest {

    @Test
    public void findTop() {
        MysqlConnector mysqlConnector = new MysqlConnector();
        SqlSession sqlSession = mysqlConnector.getSessionFactory().openSession();
        ConfigNotifyMapper configNotifyMapper = sqlSession.getMapper(ConfigNotifyMapper.class);
        Long top = configNotifyMapper.findTopByOrderByIdDesc();
        System.out.println(top);
    }

    @Test
    public void findGreat() {
        MysqlConnector mysqlConnector = new MysqlConnector();
        SqlSession sqlSession = mysqlConnector.getSessionFactory().openSession();
        ConfigNotifyMapper configNotifyMapper = sqlSession.getMapper(ConfigNotifyMapper.class);
        List<ConfigNotifyPo> first500ByIdGreaterThanOrderByIdAsc = configNotifyMapper.findNext500(10);
        System.out.println(first500ByIdGreaterThanOrderByIdAsc);
    }

}
