package com.medusa.gruul.afs.service.model.dto;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.afs.service.model.AfsOrderQueryStatus;
import com.medusa.gruul.afs.service.mp.entity.AfsOrder;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.module.app.shop.ShopType;
import io.vavr.control.Option;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/18
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AfsQueryDTO  {
    /**
     * 售后工单号、商品名称
     */
    private String keywords;
    /**
     * 售后状态
     */
    private AfsOrderQueryStatus status;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 售后工单号
     */
    private String afsNo;
    /**
     * 售后工单号
     */
    private String orderNo;

    /**
     * 供应商 id
     */
    private Long supplierId;

    /**
     * 店铺id
     */
    @JSONField(serialize = false)
    private Long shopId;

    /**
     * 买家id
     */
    @JSONField(serialize = false)
    private Long buyerId;

    /**
     * 买家昵称
     */
    private String buyerNickname;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 成交时间 开始时间
     */
    private LocalDate startTime;

    /**
     * 成交时间 开始时间
     */
    private LocalDate endTime;

    /**
     * 店铺类型
     */
    private ShopType shopType;

    /**
     * 配送模式
     */
    private DistributionMode distributionMode;

    /**
     * 导出的售后工单号
     */
    private List<String> exportAfsOrderNos;


    public LocalDateTime getStartTime() {
        return Option.of(startTime).map(LocalDate::atStartOfDay).getOrNull();
    }

    public LocalDateTime getEndTime() {
        return Option.of(endTime).map(time -> time.atTime(LocalTime.MAX)).getOrNull();
    }

    public String getAfsNo() {
        return StrUtil.trimToNull(afsNo);
    }

}
