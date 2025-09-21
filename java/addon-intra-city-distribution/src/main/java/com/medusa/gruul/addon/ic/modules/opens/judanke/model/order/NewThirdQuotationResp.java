package com.medusa.gruul.addon.ic.modules.opens.judanke.model.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/7
 */
@Getter
@Setter
@ToString
public class NewThirdQuotationResp {

    /**
     * expect_finish_time	string	是
     * 预计送达时间（按照公里数计算，具体以实际运力骑手配送为准）
     */
    private LocalDateTime expectFinishTime;

    /**
     * list	array	是
     * 运力集合
     */
    private List<NewThirdQuotationListResp> list;
}
