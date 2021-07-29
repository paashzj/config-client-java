package com.github.shoothzj.config.client.impl.postgre.spring.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author hezhangjian
 */
@Data
@Entity
@Table(name = "config_notify")
public class ConfigNotifyPo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "config_name", length = 64)
    private String configName;

    @Column(name = "config_item_id",  length = 64)
    private String configItemId;

    @Column(name = "notify_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime notifyTime;

}
