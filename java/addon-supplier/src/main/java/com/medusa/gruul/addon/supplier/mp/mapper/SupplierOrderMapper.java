package com.medusa.gruul.addon.supplier.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.supplier.model.bo.PublishProductKey;
import com.medusa.gruul.addon.supplier.model.dto.*;
import com.medusa.gruul.addon.supplier.model.vo.*;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/7/19
 */
public interface SupplierOrderMapper extends BaseMapper<SupplierOrder> {

    /**
     * 分页查询供应商订单
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    IPage<SupplierOrder> orderPage(@Param("query") OrderQueryPageDTO query);


    /**
     * 查询订单详情
     *
     * @param query 查询条件
     * @return 订单详情信息
     */
    SupplierOrder order(@Param("query") OrderDetailQueryDTO query);


    /**
     * 查询需要出库的订单数据
     *
     * @param query 查询条件
     * @return 查询需要出库的订单数据
     */
    OrderStorageVO orderStorage(@Param("query") OrderMatchQueryDTO query);


    /**
     * 待发布商品分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    IPage<PublishProductVO> publishPage(@Param("query") PublishProductQueryDTO query);

    /**
     * 查询待发布商品采购数量
     *
     * @param keys key
     * @return 查询结果
     */
    List<PublishProductKey> getPublishNum(@Param("keys") Set<PublishProductKey> keys);

    /**
     * 获取供应商时间范围内商品交易量&交易额
     *
     * @param sql 待执行的SQL
     * @return {@link SupplierGoodsTradeStaticVO}
     */
    List<SupplierGoodsTradeStaticVO> getSupplierGoodsTradeStatic(@Param("sql") String sql);

    /**
     * 获取供应商商品交易量TOP数据
     *
     * @param dto {@link SupplierGoodsTradeStaticDTO}
     * @return {@link SupplierGoodsTradeTopVO}
     */
    List<SupplierGoodsTradeTopVO> getSupplierGoodsTradeNumTopList(@Param("dto") SupplierGoodsTradeStaticDTO dto);

    List<SupplierGoodsTradeAmountTopVO> getSupplierGoodsTradeAmountTopList(@Param("dto") SupplierGoodsTradeStaticDTO dto);
}
