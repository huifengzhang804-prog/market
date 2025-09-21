package com.medusa.gruul.goods.service.model.copy;

import java.util.List;
import lombok.Data;

/**
 * @author miskw
 * @date 2023/2/1
 */
@Data
public class TbProduct {

    /**
     * 名称
     */
    private String title;

    /**
     * 图片
     */
    private List<String> images;

    /**
     * 视频
     */
    private List<String> videos;
    /**
     * sku
     */
    private List<TbSku> sku;
    /**
     * 描述
     */
    private String desc;
    /**
     * 描述图片
     */
    private List<String> descImgs;
    /**
     * 规格组
     */
    private List<TbSpecGroup> props;

}
