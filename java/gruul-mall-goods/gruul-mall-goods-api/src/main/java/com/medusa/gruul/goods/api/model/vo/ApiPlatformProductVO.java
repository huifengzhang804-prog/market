package com.medusa.gruul.goods.api.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.goods.api.model.dto.ProductExtraDTO;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : xiaoq
 * @ Description: 平台商品基础Vo
 * @ Date : 2022-05-19
 */
@Data
public class ApiPlatformProductVO implements Serializable {

    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 展示图片
     */
    private String pic;
    /**
     * 宽屏展示图片
     */
    private String widePic;
    /**
     * 画册图片，连产品图片限制为6张，以逗号分割
     */
    private String albumPics;
    /**
     * 配送方式(0--商家配送，1--快递配送 2--同城配送)
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<DistributionMode> distributionMode;
    /**
     * 所属店铺id
     */
    private Long shopId;
    /**
     * 商品统计
     */
    private ProductStatisticsVO statistics;

    /**
     * 供应商id
     */
    private Long supplierId;

    /**
     * 商品类型
     */
    private SellType sellType;


    /**
     * 附加数据
     */
    private ProductExtraDTO extra;

}
