package com.medusa.gruul.search.service.es.entity;

import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.enums.OrderType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2022/11/30
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@IndexName(value = "product_activity", keepGlobalPrefix = true)
public class EsProductActivityEntity extends EsBaseEntity {

    /**
     * 活动类型
     */
    private OrderType activityType;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 活动开始时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = FastJson2.DATETIME_PATTEN)
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = FastJson2.DATETIME_PATTEN)
    private LocalDateTime endTime;

}
