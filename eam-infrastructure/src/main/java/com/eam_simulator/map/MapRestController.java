package com.eam_simulator.map;

import com.eam_simulator.common.RoutePaths;
import com.eam_simulator.map.dto.CreateMapCommand;
import com.eam_simulator.map.dto.CreateMapRequest;
import com.eam_simulator.map.dto.CreateMapResponse;
import com.eam_simulator.map.dto.MapView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RoutePaths.MAPS)
@RequiredArgsConstructor
class MapRestController {

    private final MapFacade mapFacade;

    @PostMapping
    public ResponseEntity<CreateMapResponse> createMap(@RequestBody CreateMapRequest request) {
        MapView mapView = mapFacade.generateInitialWorld(new CreateMapCommand(request.mapName(), request.size()));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CreateMapResponse(mapView.mapName(), mapView.size(), mapView.tiles()));
    }
}
