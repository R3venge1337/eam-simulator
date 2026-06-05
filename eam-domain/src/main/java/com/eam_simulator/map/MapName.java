package com.eam_simulator.map;

import com.eam_simulator.domain.DomainErrorMessages;
import com.eam_simulator.domain.map.exceptions.EmptyFieldException;

import java.util.Objects;

record MapName(String name) {
    public MapName(String name) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new EmptyFieldException(DomainErrorMessages.INVALID_MAP_NAME);
        }
        this.name = name;
    }
}
