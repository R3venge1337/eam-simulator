package com.eam_simulator.domain.map.exceptions;

import com.eam_simulator.domain.DomainException;

public class EmptyFieldException extends DomainException {
    public EmptyFieldException(String message) {
        super(message);
    }

    public EmptyFieldException(final String message, final Object... args) {
        super(String.format(message, args));
    }

    @Override
    public String getErrorCode() {
        return "EMPTY_FIELD";
    }
}
