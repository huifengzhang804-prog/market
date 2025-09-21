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
