package com.medusa.gruul.global.model.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 完整地址抽象模型
 *
 * @author 张治保
 * @since 2024/10/26
 */
public interface Address extends Serializable {

    /**
     * 获取完整地址
     *
     * @param area    省市区
     * @param address 详细地址
     * @return 完整地址
     */
    static String full(List<String> area, String address) {
        if (CollUtil.isEmpty(area)) {
            return address == null ? StrUtil.EMPTY : address;
        }
        String join = String.join(StrUtil.EMPTY, area);
        if (address == null) {
            return join;
        }
        return join + StrUtil.SPACE + address;
    }

    /**
     * 获取省市区
     *
     * @return 省市区
     */
    List<String> getArea();

    /**
     * 获取详细地址
     *
     * @return 详细地址
     */
    String getAddress();

    /**
     * 获取省
     */
    default String province() {
        List<String> area = getArea();
        return CollUtil.isEmpty(area) ? StrUtil.EMPTY : area.get(0);
    }

    /**
     * 获取市
     */
    default String city() {
        List<String> area = getArea();
        if (CollUtil.isEmpty(area)) {
            return StrUtil.EMPTY;
        }
        //兼容直辖市 如[上海市，静安区]
        if (area.size() <= 2) {
            return area.get(0);
        }
        return area.get(1);
    }

    /**
     * 获取区
     */
    default String county() {
        List<String> area = getArea();
        if (CollUtil.isEmpty(area)) {
            return StrUtil.EMPTY;
        }
        return area.get(area.size() - 1);
    }

    /**
     * 获取完整地址
     *
     * @return 完整地址
     */
    default String fullAddress() {
        return full(getArea(), getAddress());
    }


}
