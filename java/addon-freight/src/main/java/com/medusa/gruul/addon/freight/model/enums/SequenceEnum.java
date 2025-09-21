package com.medusa.gruul.addon.freight.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 物流节点查询排序
 *
 * @author xiaoq
 * @Description 物流节点查询排序
 * @date 2022-06-13 15:28
 */
@Getter
@RequiredArgsConstructor
public enum SequenceEnum {
    /**
     * 升序
     */
    ASC("asc"),

    /**
     * 降序
     */
    DESC("desc");

    @EnumValue
    private final String value;
}
