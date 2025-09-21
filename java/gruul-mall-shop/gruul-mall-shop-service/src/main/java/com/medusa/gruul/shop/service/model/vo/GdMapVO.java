package com.medusa.gruul.shop.service.model.vo;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author wufuzhong
 * @Date 2023 2023/12/16 14:39
 * @Description 高德 ip地址查询经纬度 VO
 * {"status":"1","info":"OK","infocode":"10000","province":"北京市","city":"北京市","adcode":"110000",
 * "rectangle":"116.0119343,39.66127144;116.7829835,40.2164962"}
 */
@Getter
@Setter
@ToString
public class GdMapVO implements Serializable {

    private static final long serialVersionUID = 730546632861485529L;

    /**
     * 值为0或1,0表示失败；1表示成功
     */
    private Integer status;

    /**
     * 返回状态说明，status为0时，info返回错误原因，否则返回“OK”
     */
    private String info;

    /**
     * 返回状态说明,10000代表正确,详情参阅info状态表
     */
    private String infocode;

    /**
     * 若为直辖市则显示直辖市名称；
     * 如果在局域网 IP网段内，则返回“局域网”；
     * 非法IP以及国外IP则返回空
     */
    private String province;

    /**
     * 若为直辖市则显示直辖市名称；
     * 如果为局域网网段内IP或者非法IP或国外IP，则返回空
     */
    private String city;

    /**
     * 城市的adcode编码
     */
    private String adcode;

    /**
     * 所在城市范围的左下右上对标对
     * 116.0119343,39.66127144;116.7829835,40.2164962
     */
    private String rectangle;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    public void setRectangle(String rectangle) {
        this.rectangle = rectangle;
        if (StrUtil.isNotEmpty(rectangle) && rectangle.contains(StrUtil.COMMA)) {
            Optional<String> result = Arrays.stream(rectangle.split(";")).findFirst();
            result.ifPresent(s -> {
                if (StrUtil.isNotEmpty(s) && s.contains(StrUtil.COMMA)) {
                    String[] split = s.split(StrUtil.COMMA);
                    this.longitude = Double.valueOf(split[CommonPool.NUMBER_ZERO]);
                    this.latitude = Double.valueOf(split[CommonPool.NUMBER_ONE]);
                }
            });
        }
    }
}
