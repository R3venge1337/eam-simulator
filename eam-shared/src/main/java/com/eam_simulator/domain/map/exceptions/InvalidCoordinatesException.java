package com.eam_simulator.domain.map.exceptions;

import com.eam_simulator.domain.DomainErrorMessages;
import com.eam_simulator.domain.DomainException;

public class InvalidCoordinatesException extends DomainException {

    public InvalidCoordinatesException(int x, int y) {
        super(DomainErrorMessages.INVALID_COORDINATES_SIZE, x, y);
    }

    @Override
    public String getErrorCode() {
        return "INVALID_COORDINATES";
    }
}
