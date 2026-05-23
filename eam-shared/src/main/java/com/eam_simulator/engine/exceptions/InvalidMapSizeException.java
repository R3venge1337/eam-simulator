package com.eam_simulator.engine.exceptions;

public class InvalidMapSizeException extends DomainException {

    public InvalidMapSizeException(int width, int height) {
        super(DomainErrorMessages.INVALID_MAP_SIZE, width, height);
    }
    @Override
    public String getErrorCode() {
        return "INVALID_MAP_SIZE";
    }
}
