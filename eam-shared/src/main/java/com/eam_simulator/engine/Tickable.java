package com.eam_simulator.engine;

import org.springframework.core.Ordered;

@FunctionalInterface
public interface Tickable extends Ordered {
    void onTick(TickEmitted tick);
    
    @Override
    default int getOrder() {
        return 0;
    }
}
