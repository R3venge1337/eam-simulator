package com.eam_simulator.map;

import com.eam_simulator.engine.exceptions.DomainErrorMessages;
import com.eam_simulator.engine.exceptions.EmptyFieldException;

import java.util.Objects;

record MapName(String name) {
    public MapName(String name) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new EmptyFieldException(DomainErrorMessages.INVALID_MAP_NAME);
        }
        this.name = name;
    }
}
