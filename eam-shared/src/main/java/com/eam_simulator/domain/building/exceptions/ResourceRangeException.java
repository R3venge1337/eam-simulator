package com.eam_simulator.domain.building.exceptions;

import com.eam_simulator.domain.DomainException;

public class ResourceRangeException extends DomainException {
    public ResourceRangeException(String message) {
        super(message);
    }

    public ResourceRangeException(final String message, final Object... args) {
        super(String.format(message, args));
    }

    @Override
    public String getErrorCode() {
        return "RESOURCE_RANGE_INVALID";
    }
}
