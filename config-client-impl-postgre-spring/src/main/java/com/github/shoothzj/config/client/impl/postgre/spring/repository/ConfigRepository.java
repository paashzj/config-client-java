package com.github.shoothzj.config.client.impl.postgre.spring.repository;

import com.github.shoothzj.config.client.impl.postgre.spring.domain.ConfigPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hezhangjian
 */
@Repository
public interface ConfigRepository extends JpaRepository<ConfigPo, Long> {

    /**
     * 删除配置
     * @param configName
     */
    @Transactional
    void deleteByConfigName(String configName);

    /**
     * 查询配置
     * @param configName
     * @return
     */
    ConfigPo findByConfigName(String configName);

}
