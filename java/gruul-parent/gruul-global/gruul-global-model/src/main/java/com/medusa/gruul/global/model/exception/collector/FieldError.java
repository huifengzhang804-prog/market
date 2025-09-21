package com.medusa.gruul.global.model.exception.collector;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 字段错误信息
 *
 * @author 张治保
 * @since 2023/10/18
 */
@Getter
@Setter
@EqualsAndHashCode(of = "filed")
@Accessors(chain = true)
public class FieldError implements Comparable<FieldError>, Serializable {

    /**
     * 字段信息 主语
     */
    private String filed;

    /**
     * 错误描述信息 谓语 宾语...
     */
    private String desc;


    @Override
    public String toString() {
        return StrUtil.blankToDefault(filed, "") + desc;
    }

    @Override
    public int compareTo(@NonNull FieldError o) {
        return ObjectUtil.compare(this.filed, o.filed);
    }
}
