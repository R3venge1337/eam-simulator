package com.eam_simulator.map;

import com.eam_simulator.map.dto.MapTile;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.databind.ObjectMapper;

@Converter
class TileGridConverter implements AttributeConverter<MapTile[][], String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(MapTile[][] attribute) {
        if (attribute == null) {
            return null;
        }
        return OBJECT_MAPPER.writeValueAsString(attribute);
    }

    @Override
    public MapTile[][] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new MapTile[0][0];
        }
        return OBJECT_MAPPER.readValue(dbData, MapTile[][].class);
    }
}
