package com.github.shoothzj.config.client.impl.postgre.spring.repository;

import com.github.shoothzj.config.client.impl.postgre.spring.domain.ConfigNotifyPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hezhangjian
 */
@Repository
public interface ConfigNotifyRepository extends JpaRepository<ConfigNotifyPo, Long> {

    /**
     * find the latest notify
     * @return the largest dao
     */
    ConfigNotifyPo findTopByOrderByIdDesc();

    /**
     * Search for next 500
     * @param maxIdScanned 当前最大扫描到的Id
     * @return
     */
    List<ConfigNotifyPo> findFirst500ByIdGreaterThanOrderByIdAsc(long maxIdScanned);

}
