# com.medusa.gruul.common.geometry.GeoJson

**文件路径**: `common\geometry\GeoJson.java`

## 代码文档
///
GeoJson常量

@author 张治保
 
///


## 文件结构
```
项目根目录
└── common\geometry
    └── GeoJson.java
```

## 完整代码
```java
package com.medusa.gruul.common.geometry;

/**
 * GeoJson常量
 *
 * @author 张治保
 */
public class GeoJson {
	public static final String POINT = "Point";
	public static final String LINE_STRING = "LineString";
	public static final String POLYGON = "Polygon";

	public static final String MULTI_POINT = "MultiPoint";
	public static final String MULTI_LINE_STRING = "MultiLineString";
	public static final String MULTI_POLYGON = "MultiPolygon";

	public static final String GEOMETRY_COLLECTION = "GeometryCollection";

	public static final String TYPE = "type";

	public static final String GEOMETRIES = "geometries";

	public static final String COORDINATES = "coordinates";
}
```

# com.medusa.gruul.common.geometry.Geometry

**文件路径**: `common\geometry\Geometry.java`

## 代码文档
///
坐标系工具类

@author 张治保
 
///

///
获取GeometryFactory单例对象
     
///

///
静态内部类单例模式
     
///


## 文件结构
```
项目根目录
└── common\geometry
    └── Geometry.java
```

## 完整代码
```java
package com.medusa.gruul.common.geometry;


import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * 坐标系工具类
 *
 * @author 张治保
 */
public class Geometry {

    /**
     * 获取GeometryFactory单例对象
     */
    public static GeometryFactory factory() {
        return SingletonInstance.INSTANCE;
    }


    /**
     * 静态内部类单例模式
     */
    private static class SingletonInstance {
        private static final GeometryFactory INSTANCE = new GeometryFactory(
            new PrecisionModel(PrecisionModel.FLOATING),
            GeometryConst.COORDINATE_SYSTEM
        );
    }
}

```

# com.medusa.gruul.common.geometry.GeometryConst

**文件路径**: `common\geometry\GeometryConst.java`

## 代码文档
///
@author 张治保
date 2022/4/15
 
///

///
定位坐标系
     
///

///
坐标系 字节长度
     
///

///
几何字节最小长度
     
///


## 文件结构
```
项目根目录
└── common\geometry
    └── GeometryConst.java
```

## 完整代码
```java
package com.medusa.gruul.common.geometry;

/**
 * @author 张治保
 * date 2022/4/15
 */
public interface GeometryConst {
    /**
     * 定位坐标系
     */
    int COORDINATE_SYSTEM = 4326;
    /**
     * 坐标系 字节长度
     */
    int COORDINATE_SYSTEM_BYTE_LENGTH = 4;

    /**
     * 几何字节最小长度
     */
    int GEOMETRY_BYTE_LENGTH_MIN = 5;
}

```

# com.medusa.gruul.common.geometry.GeometryException

**文件路径**: `common\geometry\GeometryException.java`

## 代码文档
///
@author 张治保
date 2022/3/25
 
///


## 文件结构
```
项目根目录
└── common\geometry
    └── GeometryException.java
```

## 完整代码
```java
package com.medusa.gruul.common.geometry;

import com.medusa.gruul.global.model.exception.GlobalException;

/**
 * @author 张治保
 * date 2022/3/25
 */
public class GeometryException extends GlobalException {

    public GeometryException(int code, String message) {
        super(code, message);
    }

    public GeometryException(String message) {
        super(message);
    }

    public GeometryException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public GeometryException(String message, Throwable cause) {
        super(message, cause);
    }
}

```

# com.medusa.gruul.common.geometry.GeometryI18NLoader

**文件路径**: `common\geometry\GeometryI18NLoader.java`

## 代码文档
///
@author 张治保
date 2023/6/16
 
///


## 文件结构
```
项目根目录
└── common\geometry
    └── GeometryI18NLoader.java
```

## 完整代码
```java
package com.medusa.gruul.common.geometry;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * @author 张治保
 * date 2023/6/16
 */
public class GeometryI18NLoader implements I18NPropertiesLoader {
	@Override
	@NonNull
	public Set<String> paths() {
		return Set.of("i18n/geo");
	}
}

```

# com.medusa.gruul.common.geometry.GeometrySpringListener

**文件路径**: `common\geometry\GeometrySpringListener.java`

## 代码文档
///
@author 张治保
date 2023/7/17
 
///


## 文件结构
```
项目根目录
└── common\geometry
    └── GeometrySpringListener.java
```

## 完整代码
```java
package com.medusa.gruul.common.geometry;

import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.common.geometry.rw.GeometryReader;
import com.medusa.gruul.common.geometry.rw.GeometryWriter;
import com.medusa.gruul.common.spring.listener.functions.GruulSpringListener;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.springframework.boot.ConfigurableBootstrapContext;

/**
 * @author 张治保
 * date 2023/7/17
 */
public class GeometrySpringListener extends GruulSpringListener {

	private static final GeometryReader READER = new GeometryReader();
	private static final GeometryWriter WRITER = new GeometryWriter();

	@Override
	public void starting(ConfigurableBootstrapContext bootstrapContext) {
		//几何
		JSON.register(Geometry.class, READER);
		JSON.register(Geometry.class, WRITER);
		//点
		JSON.register(Point.class, READER);
		JSON.register(Point.class, WRITER);
		//多边形
		JSON.register(Polygon.class, READER);
		JSON.register(Polygon.class, WRITER);
	}
}

```

# com.medusa.gruul.common.geometry.GeometryTypeHandler

**文件路径**: `common\geometry\GeometryTypeHandler.java`

## 代码文档
///
Geometry 数据映射处理器

@author 张治保
 
///


## 文件结构
```
项目根目录
└── common\geometry
    └── GeometryTypeHandler.java
```

## 完整代码
```java
package com.medusa.gruul.common.geometry;

import com.medusa.gruul.global.i18n.I18N;
import com.vividsolutions.jts.io.ByteOrderValues;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.*;

/**
 * Geometry 数据映射处理器
 *
 * @author 张治保
 */
@MappedTypes({com.vividsolutions.jts.geom.Geometry.class})
@Slf4j
public class GeometryTypeHandler implements TypeHandler<com.vividsolutions.jts.geom.Geometry> {

	@Override
	public void setParameter(PreparedStatement preparedStatement, int index, com.vividsolutions.jts.geom.Geometry geometry, JdbcType jdbcType) throws SQLException {
		if (geometry == null) {
			preparedStatement.setNull(index, Types.VARCHAR);
		} else {
			byte[] geometryBytes = new WKBWriter(2, ByteOrderValues.LITTLE_ENDIAN).write(geometry);
			byte[] wkb = new byte[geometryBytes.length + GeometryConst.COORDINATE_SYSTEM_BYTE_LENGTH];
			//设置 sr id 为4326
			ByteOrderValues.putInt(
					GeometryConst.COORDINATE_SYSTEM,
					wkb,
					ByteOrderValues.LITTLE_ENDIAN
			);
			System.arraycopy(
					geometryBytes, 0,
					wkb, GeometryConst.COORDINATE_SYSTEM_BYTE_LENGTH,
					geometryBytes.length
			);
			preparedStatement.setBytes(
					index,
					wkb
			);
		}
	}

	@Override
	public com.vividsolutions.jts.geom.Geometry getResult(ResultSet resultSet, String column) throws SQLException {
		return getPoint(resultSet.getBytes(column));
	}

	@Override
	public com.vividsolutions.jts.geom.Geometry getResult(ResultSet resultSet, int index) throws SQLException {
		return getPoint(resultSet.getBytes(index));
	}

	@Override
	public com.vividsolutions.jts.geom.Geometry getResult(CallableStatement callableStatement, int i) throws SQLException {
		return getPoint(callableStatement.getBytes(i));
	}

	private com.vividsolutions.jts.geom.Geometry getPoint(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		if (bytes.length < GeometryConst.GEOMETRY_BYTE_LENGTH_MIN) {
			throw new GeometryException(I18N.msg("geometry.data.not.format"));
		}
		try {
			return getGeometryByBytes(bytes);
		} catch (ParseException e) {
			throw new GeometryException(I18N.msg("geometry.data.not.format"), e);
		}

	}

	private com.vividsolutions.jts.geom.Geometry getGeometryByBytes(byte[] geometryBytes) throws ParseException {
		byte[] geomBytes =
				ByteBuffer.allocate(geometryBytes.length - 4)
						.order(ByteOrder.LITTLE_ENDIAN)
						.put(
								geometryBytes,
								GeometryConst.COORDINATE_SYSTEM_BYTE_LENGTH,
								geometryBytes.length - GeometryConst.COORDINATE_SYSTEM_BYTE_LENGTH
						)
						.array();
		return new WKBReader(Geometry.factory()).read(geomBytes);
	}
}

```

# com.medusa.gruul.common.geometry.rw.GeometryReader

**文件路径**: `geometry\rw\GeometryReader.java`

## 代码文档
///
@author 张治保
date 2023/4/8
 
///


## 文件结构
```
项目根目录
└── geometry\rw
    └── GeometryReader.java
```

## 完整代码
```java
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

```

# com.medusa.gruul.common.geometry.rw.GeometryWriter

**文件路径**: `geometry\rw\GeometryWriter.java`

## 代码文档
///
Geometry 序列化器

@author 张治保
date 2023/4/8
 
///


## 文件结构
```
项目根目录
└── geometry\rw
    └── GeometryWriter.java
```

## 完整代码
```java
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

```

