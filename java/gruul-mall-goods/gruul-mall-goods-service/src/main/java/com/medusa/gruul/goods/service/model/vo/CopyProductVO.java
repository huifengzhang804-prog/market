package com.medusa.gruul.goods.service.model.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.medusa.gruul.storage.api.dto.SpecGroupDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author miskw
 * @date 2023/2/8
 */
@Data
@Accessors(chain = true)
public class CopyProductVO {
    /**
     * id
     */
    private Long id;
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
     * 视频url
     */
    private String videoUrl;
    /**
     * 销量
     */
    private Integer sale;
    /**
     * 商品详情
     */
    private String detail;
    /**
     * 卖点描述
     */
    private String saleDescribe;
    /**
     * 评分
     */
    private BigDecimal score;

    /**
     * 规格组
     */
    private List<SpecGroupDTO> specGroups;

    /**
     * sku列表
     */
    private List<CopySkuVO> skus;

    /**
     * 用于临时存储商品主图
     */
    @JSONField(serialize = false, deserialize = false)
    private List<String> originalImgUrls;

   
}
