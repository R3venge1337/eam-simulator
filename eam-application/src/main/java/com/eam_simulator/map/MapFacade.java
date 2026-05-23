package com.eam_simulator.map;

import com.eam_simulator.map.dto.CreateMapCommand;
import com.eam_simulator.map.dto.MapView;

public interface MapFacade {
    MapView generateInitialWorld(CreateMapCommand command);
}
