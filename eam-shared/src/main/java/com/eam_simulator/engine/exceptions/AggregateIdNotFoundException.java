package com.eam_simulator.engine.exceptions;

public class AggregateIdNotFoundException extends DomainException {
    public AggregateIdNotFoundException(String message) {
        super(message);
    }

    public AggregateIdNotFoundException(final String message, final Object... args) {
        super(String.format(message, args));
    }
    @Override
    public String getErrorCode() {
        return "AGGREGATE_ID_NOT_FOUND";
    }

}
