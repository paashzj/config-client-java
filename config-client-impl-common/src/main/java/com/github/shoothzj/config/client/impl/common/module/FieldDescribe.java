package com.github.shoothzj.config.client.impl.common.module;

import lombok.Getter;
import lombok.Setter;

/**
 * @author hezhangjian
 */
@Setter
@Getter
public class FieldDescribe {

    private String name;

    /**
     * 用于持久化系统
     */
    private String persistentName;

    private FieldType fieldType;

    private boolean anonymous;

    private boolean secret;

    private boolean required;

    public FieldDescribe() {
    }

    public FieldDescribe(String name) {
        this(name, name);
    }

    public FieldDescribe(String name, String persistentName) {
        this.name = name;
        this.persistentName = persistentName;
    }
}
