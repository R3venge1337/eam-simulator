package com.eam_simulator.domain.map.exceptions;

import com.eam_simulator.domain.DomainException;

public class InvalidElevationLevelException extends DomainException {
    public InvalidElevationLevelException(String message) {
        super(message);
    }

    public InvalidElevationLevelException(final String message, final Object... args) {
        super(String.format(message, args));
    }

    @Override
    public String getErrorCode() {
        return "INVALID_ELEVATION";
    }
}
