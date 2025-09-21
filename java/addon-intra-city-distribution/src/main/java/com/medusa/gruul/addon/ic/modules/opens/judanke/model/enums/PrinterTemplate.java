package com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/7
 */
@Getter
@RequiredArgsConstructor
public enum PrinterTemplate {
    /**
     * 195107	外卖小票通用顾客联
     */
    A("195107"),
    /**
     * 195108	外卖小票通用商家联
     */
    B("195108"),
    /**
     * 195109	外卖小票通用后厨联
     */
    C("195109"),
    /**
     * 195113	外卖小票通用催单联
     */
    D("195113"),
    /**
     * 195114	外卖小票通用取消联
     */
    E("195114"),
    /**
     * 195117	外卖小票通用顾客联80
     */
    F("195117"),
    /**
     * 195118	外卖小票通用商家联80
     */
    G("195118"),
    /**
     * 195119	外卖小票通用后厨联80
     */
    H("195119"),
    /**
     * 195120	外卖小票通用催单联80
     */
    I("195120"),
    /**
     * 195121	外卖小票通用取消联80
     */
    J("195121"),
    /**
     * 195123	消息通知小票
     */
    K("195123"),
    /**
     * 195124	消息通知小票80
     */
    L("195124"),
    /**
     * 195133	外卖小票通用申请取消联
     */
    M("195133"),
    /**
     * 195134	外卖小票通用申请取消联80
     */
    N("195134"),
    /**
     * 195135	外卖A4小票通用
     */
    O("195135"),
    /**
     * 195136	外卖A4小票鲜花
     */
    P("195136"),
    /**
     * 195137	外卖A4小票蛋糕
     */
    Q("195137"),
    /**
     * 195138	外卖A4小票礼品
     */
    R("195138");

    /**
     * 模板 id
     */
    @JSONField
    private final String value;
}
