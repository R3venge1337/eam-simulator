package com.eam_simulator.map;

import com.eam_simulator.map.dto.CreateMapCommand;
import com.eam_simulator.map.dto.CreateMapRequest;
import com.eam_simulator.map.dto.GenerationSettings;
import org.springframework.stereotype.Component;

@Component
class MapRequestMapper {
    public CreateMapCommand toCommand(CreateMapRequest request) {
        var hillReq = request.settings().hillSettings();
        var lakeReq = request.settings().lakesSettings();
        var mountReq = request.settings().mountainsSettings();
        var sandReq = request.settings().sandSettings();

        GenerationSettings envSettings = new GenerationSettings(
                hillReq, lakeReq, mountReq, sandReq
        );
        return new CreateMapCommand(
                request.mapName(),
                request.size(),
                envSettings
        );
    }
}
