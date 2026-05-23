package com.eam_simulator.engine.exceptions;

public class InvalidCoordinatesException extends DomainException {

    public InvalidCoordinatesException(int x, int y) {
        super(DomainErrorMessages.INVALID_COORDINATES_SIZE, x, y);
    }

    @Override
    public String getErrorCode() {
        return "INVALID_COORDINATES";
    }
}
