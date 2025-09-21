package com.medusa.gruul.addon.ic.modules.ic.model.vo;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICCourier;
import com.medusa.gruul.addon.ic.modules.opens.uupt.CoordinateTransform;
import com.medusa.gruul.common.geometry.Geometry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 配送员位置信息
 *
 * @author 张治保
 * @since 2024/8/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CourierVO {

    /**
     * 配送员信息
     */
    private ICCourier courier;

    /**
     * 距离目标位置的距离 单位米
     */
    private Long distance;

    /**
     * 定位
     */
    private Point location;

    /**
     * 预计送达时间
     */
    private LocalDateTime expectTime;

    /**
     * 预计还有送达时长 单位分钟 为负代表已超时
     */
    private Long minutes;

    /**
     * 百度坐标系 转高德坐标系的 Point
     *
     * @param uuptLoc uu 跑腿的定位信息 百度地图坐标系经纬度（格式:113.71776,34.767501）
     * @return 高德坐标系的 Point
     */
    public static Point ofBd09(String uuptLoc) {
        List<String> lngLatStr = StrUtil.split(uuptLoc, StrPool.COMMA);
        double[] lngLat = CoordinateTransform.bd09togcj02(Double.parseDouble(lngLatStr.get(0)), Double.parseDouble(lngLatStr.get(1)));
        return Geometry.factory().createPoint(
                new Coordinate(
                        lngLat[0]
                        , lngLat[1]
                )
        );
    }

    public static Point center(Point point1, Point point2) {
        return Geometry.factory()
                .createLineString(new Coordinate[]{
                        point1.getCoordinate(),
                        point2.getCoordinate()
                }).getCentroid();
    }

    public CourierVO setExpectTime(LocalDateTime expectTime) {
        this.expectTime = expectTime;
        if (expectTime == null) {
            return this;
        }
        this.minutes = Duration.between(LocalDateTime.now(), expectTime).toMinutes();
        return this;
    }
}
