package com.github.shoothzj.config.client.impl.common.module;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

/**
 * @author shoothzj
 */
@Data
@AllArgsConstructor
public class IdVersion {

    private String id;

    private int version;

    public IdVersion() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IdVersion idVersion = (IdVersion) o;
        return version == idVersion.version && Objects.equals(id, idVersion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version);
    }

}
