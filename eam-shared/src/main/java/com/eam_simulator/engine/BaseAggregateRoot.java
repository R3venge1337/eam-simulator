package com.eam_simulator.engine;

import com.eam_simulator.engine.exceptions.AggregateIdNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.eam_simulator.engine.exceptions.DomainErrorMessages.AGGREGATE_ID_NOT_FOUND;

public abstract class BaseAggregateRoot implements AggregateRoot {
    private final UUID id;
    private final List<DomainEvent> events = new ArrayList<>();

    protected BaseAggregateRoot() {
        this.id = UUID.randomUUID();
    }

    protected BaseAggregateRoot(UUID id) {
        if (id == null) {
            throw new AggregateIdNotFoundException(AGGREGATE_ID_NOT_FOUND);
        }
        this.id = id;
    }

    @Override
    public UUID getId() {
        return id;
    }

    protected void registerEvent(DomainEvent event) {
        this.events.add(event);
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> capturedEvents = new ArrayList<>(events);
        events.clear();
        return Collections.unmodifiableList(capturedEvents);
    }
}
