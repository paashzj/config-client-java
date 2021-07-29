package com.github.shoothzj.config.client.api;

/**
 * @author shoothzj
 */
public class BaseConfig {

    protected String id;

    protected Integer version;

    public BaseConfig() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
