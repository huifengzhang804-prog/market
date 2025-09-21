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
