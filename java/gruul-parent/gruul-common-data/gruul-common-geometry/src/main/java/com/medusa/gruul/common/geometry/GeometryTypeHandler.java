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
