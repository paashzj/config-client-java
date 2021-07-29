package com.github.shoothzj.config.client.impl.postgre.spring.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author hezhangjian
 */
@Data
@Entity
@Table(name = "config", uniqueConstraints = @UniqueConstraint(columnNames = "config_name"))
public class ConfigPo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "config_name", length = 64)
    private String configName;

    @Column(columnDefinition = "TEXT", length = 2048)
    private String schema;

    private int version;

}
