package com.medusa.gruul.global.model.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 执行链上下文
 *
 * @author 张治保
 * date 2022/8/15
 */
@Getter
@Setter
@ToString
public class FilterContext<Context> implements Serializable {

    /**
     * shopId
     */
    private Long shopId;

    /**
     * 是否跳出当前执行链
     */
    private boolean breakIt;

    /**
     * 自定义数据载体
     */
    private Context data;

}
