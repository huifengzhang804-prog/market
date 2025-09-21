package com.medusa.gruul.addon.supplier.mp.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.supplier.model.bo.PublishProductKey;
import com.medusa.gruul.addon.supplier.model.dto.*;
import com.medusa.gruul.addon.supplier.model.enums.InvoiceStatus;
import com.medusa.gruul.addon.supplier.model.enums.PackageStatus;
import com.medusa.gruul.addon.supplier.model.enums.SupplierError;
import com.medusa.gruul.addon.supplier.model.vo.*;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrder;
import com.medusa.gruul.addon.supplier.mp.mapper.SupplierOrderMapper;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierOrderService;
import com.medusa.gruul.addon.supplier.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/7/19
 */
@Service
public class SupplierOrderServiceImpl extends ServiceImpl<SupplierOrderMapper, SupplierOrder> implements ISupplierOrderService {

    @Override
    public IPage<SupplierOrder> orderPage(OrderQueryPageDTO query) {
        return baseMapper.orderPage(query);
    }

    @Override
    public SupplierOrder order(OrderDetailQueryDTO query) {
        return baseMapper.order(query);
    }

    @Override
    public OrderStorageVO orderStorage(OrderMatchQueryDTO query) {
        return baseMapper.orderStorage(query);
    }

    @Override
    public IPage<PublishProductVO> publishPage(PublishProductQueryDTO query) {
        return baseMapper.publishPage(query);
    }

    @Override
    public List<PublishProductKey> getPublishNum(Set<PublishProductKey> keys) {
        return baseMapper.getPublishNum(keys);
    }

    /**
     * 获取供应商时间范围内商品交易量&交易额
     *
     * @param dto {@link SupplierTradeStaticDTO}
     * @return {@link SupplierGoodsTradeStaticVO}
     */
    @Override
    public List<SupplierGoodsTradeStaticVO> getSupplierGoodsTradeStatic(SupplierTradeStaticDTO dto) {
        // 根据起始日期组装sql
        LocalDate begin = DateUtils.convert2Date(dto.getBeginTime(), DateUtils.DATE_FORMATTER);
        LocalDate end = DateUtils.convert2Date(dto.getEndTime(), DateUtils.DATE_FORMATTER);
        SupplierError.BEGIN_DATE_CAN_NOT_BE_AFTER_END_DATE.trueThrow(begin.isAfter(end));
        StringBuffer finalSql = generateTradeStaticSql(dto, begin, end);
        return baseMapper.getSupplierGoodsTradeStatic(finalSql.toString());
    }

    /**
     * 获取供应商商品交易量TOP数据
     *
     * @param dto {@link SupplierGoodsTradeStaticDTO}
     * @return {@link SupplierGoodsTradeTopVO}
     */
    @Override
    public List<SupplierGoodsTradeTopVO> getSupplierGoodsTradeNumTopList(SupplierGoodsTradeStaticDTO dto) {
        return this.baseMapper.getSupplierGoodsTradeNumTopList(dto);
    }

    /**
     * 获取供应商商品交易金额TOP数据
     *
     * @param dto {@link SupplierGoodsTradeStaticDTO}
     * @return {@link SupplierGoodsTradeAmountTopVO}
     */
    @Override
    public List<SupplierGoodsTradeAmountTopVO> getSupplierGoodsTradeAmountTopList(SupplierGoodsTradeStaticDTO dto) {
        return this.baseMapper.getSupplierGoodsTradeAmountTopList(dto);
    }

    /**
     * 根据主单号查询订单
     *
     * @param mainNos 主单号
     * @param orderNo 订单号
     * @return 订单集合
     */
    @Override
    public List<SupplierOrder> getOrderByMainNo(String orderNo, Set<String> mainNos) {
        LambdaQueryWrapper<SupplierOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CollUtil.isNotEmpty(mainNos), SupplierOrder::getMainNo, mainNos);
        queryWrapper.eq(StrUtil.isNotEmpty(orderNo), SupplierOrder::getNo, orderNo);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public void syncOrderInvoiceStatus(String orderNo, Integer invoiceStatusCode) {
        lambdaUpdate()
                .set(SupplierOrder::getInvoiceStatus, InvoiceStatus.getByValue(invoiceStatusCode))
                //申请开票时间
                .set(InvoiceStatus.REQUEST_IN_PROCESS.getValue().equals(invoiceStatusCode),
                        SupplierOrder::getApplyInvoiceTime, LocalDateTime.now())
                //开票成功时间
                .set(InvoiceStatus.SUCCESSFULLY_INVOICED.getValue().equals(invoiceStatusCode),
                        SupplierOrder::getInvoiceTime,LocalDateTime.now())
                .eq(SupplierOrder::getNo, orderNo)
                .update();
    }

    private StringBuffer generateTradeStaticSql(SupplierTradeStaticDTO dto, LocalDate begin, LocalDate end) {
        StringBuffer finalSql = new StringBuffer("select * from (");
        StringBuffer leftSql = new StringBuffer();
        for (; !begin.isAfter(end); begin = begin.plusDays(1L)) {
            leftSql.append("select '").append(DateUtils.convert2String(begin, DateUtils.DATE_FORMATTER)).append("' as xDate  ");
            if (!begin.isEqual(end)) {
                leftSql.append(" union\n ");
            }
        }
        finalSql.append(leftSql).append(" ) a left join ");
        finalSql.append(" ( select ");
        finalSql.append(" count(*) as tradeNumber, sum(deal_price * num) as tradeAmount, cast(create_time AS date) as create_time ")
                .append(" from ")
                .append(" t_supplier_order_item ")
                .append(" where ")
                .append(" supplier_id = ").append(dto.getShopId()).append(" and ")
                .append(" create_time BETWEEN '").append(dto.getBeginTimeMill()).append("' and '").append(dto.getEndTimeMill()).append("' and ")
                .append(" package_status = ").append(PackageStatus.COMPLETED.getValue())
                .append(" group by cast(create_time AS date) ")
                .append(" ) b on a.xDate = b.create_time ");
        return finalSql;
    }
}
