package com.medusa.gruul.addon.supplier.modules.overview.service;

import com.medusa.gruul.addon.supplier.model.dto.SupplierGoodsTradeStaticDTO;
import com.medusa.gruul.addon.supplier.model.dto.SupplierTradeStaticDTO;
import com.medusa.gruul.addon.supplier.model.dto.SupplierNewProductCountDTO;
import com.medusa.gruul.addon.supplier.model.enums.DateRangeType;
import com.medusa.gruul.addon.supplier.model.vo.SupplierGoodsTradeAmountTopVO;
import com.medusa.gruul.addon.supplier.model.vo.SupplierGoodsTradeStaticVO;
import com.medusa.gruul.addon.supplier.model.vo.SupplierGoodsTradeTopVO;
import com.medusa.gruul.addon.supplier.model.vo.ToDoListVO;

import java.util.List;

/**
 * <p>供应商经营概况服务接口</p>
 * @author An.Yan
 */
public interface ISupplierOverviewService {


    /**
     * 按照店铺统计当天新增咨询数
     * @param supplierShopId 供应商店铺ID
     * @return 当天新增咨询数
     */
    Integer getInquiryNumberByShopId(Long supplierShopId);


    /**
     * 取时间范围内新增的商品数量
     * @param shopId 店铺ID
     * @param dateRangeType 日期范围
     * @return {@link Integer}新增的商品数量
     */
    Integer getNewCreatedProductCount(Long shopId, DateRangeType dateRangeType);

    /**
     * 统计今日待办数据
     * @param shopId 店铺ID
     * @return {@link ToDoListVO}
     */
    ToDoListVO getToDoList(Long shopId, DateRangeType dateRangeType);

    /**
     * 获取供应商时间范围内商品交易量&交易额
     * @param dto {@link SupplierTradeStaticDTO}
     * @return {@link SupplierGoodsTradeStaticVO}
     */
    List<SupplierGoodsTradeStaticVO> getSupplierGoodsTradeStatic(SupplierTradeStaticDTO dto);

    /**
     * 获取供应商商品交易量TOP数据
     * @param dto {@link SupplierGoodsTradeStaticDTO}
     * @return {@link SupplierGoodsTradeTopVO}
     */
    List<SupplierGoodsTradeTopVO> getSupplierGoodsTradeNumTopList(SupplierGoodsTradeStaticDTO dto);

    /**
     * 获取供应商商品交易金额TOP数据
     * @param dto {@link SupplierGoodsTradeStaticDTO}
     * @return {@link SupplierGoodsTradeAmountTopVO}
     */
    List<SupplierGoodsTradeAmountTopVO> getSupplierGoodsTradeAmountTopList(SupplierGoodsTradeStaticDTO dto);

    /**
     * 获取所有供应商采购单数量
     * @return {@link Long}
     */
    Long getPurchaseOrderCount();
}
