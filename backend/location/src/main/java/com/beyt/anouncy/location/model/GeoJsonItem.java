package com.beyt.anouncy.location.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoJsonItem {
    private String type;
    private Geometry geometry;
    private Map<String, String> properties;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Geometry {
        private String type;
        private Object coordinates;
    }
}
