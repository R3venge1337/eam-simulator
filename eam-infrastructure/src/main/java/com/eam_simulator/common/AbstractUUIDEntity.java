package com.eam_simulator.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

@MappedSuperclass
@Getter
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public abstract class AbstractUUIDEntity extends AbstractEntityId {

    @EqualsAndHashCode.Include
    @Setter
    @Column(name = "uuid", unique = true, nullable = false)
    protected UUID uuid;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + "uuid=" + uuid + ')';
    }
}
