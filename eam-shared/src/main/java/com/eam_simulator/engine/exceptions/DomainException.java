package com.eam_simulator.engine.exceptions;

import java.util.UUID;

public abstract class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(final UUID uuid) {
        super(String.format("No entity with UUID: %s", uuid));
    }

    public DomainException(final String message, final Object... args) {
        super(String.format(message, args));
    }

    public abstract String getErrorCode();
}
