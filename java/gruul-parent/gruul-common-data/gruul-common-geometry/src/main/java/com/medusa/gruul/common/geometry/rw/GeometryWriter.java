package com.medusa.gruul.common.geometry.rw;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.medusa.gruul.common.fastjson2.TypeNameHash;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.lang.reflect.Type;

import static com.medusa.gruul.common.geometry.GeoJson.*;

/**
 * Geometry 序列化器
 *
 * @author 张治保
 * date 2023/4/8
 */
public class GeometryWriter implements ObjectWriter<Geometry> {

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            jsonWriter.writeNull();
            return;
        }
        if (jsonWriter.isWriteTypeInfo(object, fieldType, features)) {
            TypeNameHash typeNameHash = TypeNameHash.get(object.getClass());
            jsonWriter.writeTypeName(typeNameHash.typeNameJSONB(), typeNameHash.typeNameHash());
        }
        jsonWriter.write(this.toObject(object));
    }

    private JSONObject toObject(Object value) {
        if (value instanceof Point point) {
            return point(point);
        }
        if (value instanceof Polygon polygon) {
            return polygon(polygon);
        }
        throw new JSONException("GeometryWriter-> GeometryWriter not support type: " + value.getClass().getName());

    }

    private JSONObject polygon(Polygon polygon) {
        Coordinate[] coordinates = polygon.getCoordinates();
        double[][] coordinatePoint = new double[coordinates.length][];
        for (int index = 0; index < coordinates.length; index++) {
            coordinatePoint[index] = coordinate(coordinates[index]);
        }
        return new JSONObject()
                .fluentPut(TYPE, POLYGON)
                .fluentPut(COORDINATES, coordinatePoint);
    }

    private JSONObject point(Point point) {
        return new JSONObject()
                .fluentPut(TYPE, POINT)
                .fluentPut(COORDINATES, coordinate(point.getCoordinate()));
    }

    private double[] coordinate(Coordinate coordinate) {
        if (Double.isNaN(coordinate.z)) {
            return new double[]{coordinate.x, coordinate.y};
        }
        return new double[]{coordinate.x, coordinate.y, coordinate.z};
    }


}
