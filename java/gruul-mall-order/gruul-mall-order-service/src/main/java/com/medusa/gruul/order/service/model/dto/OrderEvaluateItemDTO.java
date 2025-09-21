package com.medusa.gruul.order.service.model.dto;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.base.ProductSkuKey;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 订单商品评价
 *
 * @author wudi
 */
@Data
@ToString
@Accessors(chain = true)
public class OrderEvaluateItemDTO {

    /**
     * 订单商品项 itemId
     */
    @NotNull
    private ProductSkuKey key;
    /**
     * 评论内容
     */
    @NotNull
    @Size(max = 500)
    private String comment;

    /**
     * 规格
     */
    private List<String> specs;

    /**
     * 上传的文件（图片/视频）
     */
    private List<String> medias;
    /**
     * "商品评分"
     */
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rate;

    /**
     * 规格转换为字符串
     *
     * @param specs 规格
     * @return 字符串
     */
    public static String specsToString(List<String> specs) {
        return CollUtil.isEmpty(specs) ? StrUtil.EMPTY : StrUtil.join(StrUtil.COMMA, specs);
    }


}
