package com.medusa.gruul.addon.ic.modules.opens.uupt.model.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/10
 */
@Getter
@Setter
@ToString
public class OpenCityListResp {
    /**
     * 开放城市列表
     */
    private List<City> openCityList;

    @Getter
    @Setter
    @ToString
    public static class City {
        /**
         * 城市名
         */
        private String cityName;

        /**
         * 区县名
         */
        private String countyName;

    }
}
