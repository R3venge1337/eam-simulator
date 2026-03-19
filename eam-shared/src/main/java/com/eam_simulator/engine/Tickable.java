package com.eam_simulator.engine;

@FunctionalInterface
public interface Tickable {
    void onTick(TickEmitted tick);
}
