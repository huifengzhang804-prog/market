package com.medusa.gruul.common.geometry.rw;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.medusa.gruul.common.geometry.GeoJson;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.medusa.gruul.common.geometry.GeoJson.TYPE;

/**
 * @author 张治保
 * date 2023/4/8
 */
public class GeometryReader implements ObjectReader<Geometry> {


    @Override
    public Geometry readJSONBObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        return readObject(jsonReader, fieldType, fieldName, features);
    }

    @Override
    public Geometry readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        return createInstance(jsonReader.readObject(), features);
    }

    @Override
    public Geometry createInstance(Map map, long features) {
        if (map == null) {
            return null;
        }
        JSONObject json = new JSONObject(map);
        String type = json.getString(TYPE);
        if (type == null || type.isEmpty()) {
            return null;
        }
        return switch (type) {
            case GeoJson.POINT -> point(json);
            case GeoJson.POLYGON -> polygon(json);
            default -> throw new IllegalArgumentException("GeometryReader-> type is not support: " + type);
        };
    }


    private Point point(JSONObject json) {
        Coordinate coordinate = coordinate(json.getJSONArray(GeoJson.COORDINATES));
        if (coordinate == null) {
            return null;
        }
        return com.medusa.gruul.common.geometry.Geometry.factory().createPoint(coordinate);
    }


    private Polygon polygon(JSONObject json) {
        JSONArray jsonArray = json.getJSONArray(GeoJson.COORDINATES);
        List<Coordinate> coordinates = new ArrayList<>(jsonArray.size());
        for (int index = 0; index < jsonArray.size(); index++) {
            Coordinate coordinate = coordinate(jsonArray.getJSONArray(index));
            if (coordinate == null) {
                continue;
            }
            coordinates.add(coordinate);
        }
        if (coordinates.isEmpty()) {
            return null;
        }
        return com.medusa.gruul.common.geometry.Geometry.factory().createPolygon(
                coordinates.toArray(new Coordinate[0])
        );
    }

    private Coordinate coordinate(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.isEmpty() || jsonArray.size() < 2) {
            return null;
        }
        double x = jsonArray.getDoubleValue(0);
        double y = jsonArray.getDoubleValue(1);
        return jsonArray.size() > 2 ?
                new Coordinate(x, y, jsonArray.getDoubleValue(2)) :
                new Coordinate(x, y);
    }

}
