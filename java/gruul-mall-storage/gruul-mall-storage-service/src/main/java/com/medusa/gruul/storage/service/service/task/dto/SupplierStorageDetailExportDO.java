package com.medusa.gruul.storage.service.service.task.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author jipeng
 * @since 2025/3/18
 */
@Data
public class SupplierStorageDetailExportDO {
    //    编号
    @ExcelProperty(value = "编号")
    private String index;
    //    单据编号
    @ExcelProperty(value = "单据编号")
    private String billNo;
    //    商品编号(SPUID)
    @ExcelProperty(value = "商品编号(SPUID)")
    private String spuId;
    //    商品名称
    @ExcelProperty(value = "商品名称")
    private String spuName;
    //    规格
    @ExcelProperty(value = "规格")
    private String spec;
    //    规格编号(SKUID)
    @ExcelProperty(value = "规格编号(SKUID)")
    private String skuId;
    //    出入库数
    @ExcelProperty(value = "出入库数")
    private String stockChangeNum;
    //    出入库类型
    @ExcelProperty(value = "出入库类型")
    private String stockChangeType;
    //    商品类型
    @ExcelProperty(value = "商品类型")
    private String spuType;
    //    销售方式
    @ExcelProperty(value = "销售方式")
    private String sellType;
    //    关联订单号
    @ExcelProperty(value = "关联订单号")
    private String orderNo;
    //    出入库时间
    @ExcelProperty(value = "出入库时间")
    private String stockChangeTime;

}
