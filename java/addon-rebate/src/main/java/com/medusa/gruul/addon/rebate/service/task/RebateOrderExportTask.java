package com.medusa.gruul.addon.rebate.service.task;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.medusa.gruul.addon.rebate.model.dto.RebateOrderExportDTO;
import com.medusa.gruul.addon.rebate.model.dto.RebateOrderQueryDTO;
import com.medusa.gruul.addon.rebate.model.enums.RebateOrderStatus;
import com.medusa.gruul.addon.rebate.model.enums.SettlementStatus;
import com.medusa.gruul.addon.rebate.mp.entity.RebateOrder;
import com.medusa.gruul.addon.rebate.mp.entity.RebateOrderItem;
import com.medusa.gruul.addon.rebate.service.RebateOrderHandlerService;
import com.medusa.gruul.carrier.pigeon.api.model.dto.UploadParamDTO;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.order.api.entity.OrderDiscount;
import com.medusa.gruul.order.api.entity.OrderDiscountItem;
import com.medusa.gruul.order.api.enums.DiscountSourceType;
import com.medusa.gruul.order.api.model.OrderInfoDTO;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.overview.api.enums.DataExportStatus;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

/**
 * 消费返利订单的导出任务
 * @author jipeng
 * @since 2025/2/22
 */
@RequiredArgsConstructor
@Slf4j
public class RebateOrderExportTask implements Runnable{

    public static final String SHEET_NAME = "SheetJS";
    /**
     * 一万
     */
    public static final Integer TEN_THOUSAND = 10000;
    public static final String EMPTY = "";
    private static final String TEMP_FILE_PATH = "temp/消费返利订单.xlsx";

    /**
     * 订单生成记录id
     */
    private final Long exportRecordId;
    private final DataExportRecordRpcService dataExportRecordRpcService;
    private final RebateOrderHandlerService rebateOrderHandlerService;
    private final RebateOrderQueryDTO queryDto;
    private final UaaRpcService uaaRpcService;
    private final OrderRpcService orderRpcService;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final GoodsRpcService goodsRpcService;

    @Override
    public void run() {
        File tempExcel = generateTempExcelFile();

        taskRun(tempExcel);
    }

    @SneakyThrows
    private void taskRun(File tempExcel) {
        DataExportRecordDTO update = new DataExportRecordDTO();
        update.setId(exportRecordId);
        try {
            int count = generateExcel(tempExcel);

            log.debug("导出成功,数据条数为:{}", count);
            //上传文件到OSS
            String path = uploadFileToOSS(tempExcel);
            update.setFilePath(path);
            update.setDataCount(count);
            update.setFileSize(Long.valueOf(tempExcel.length()).intValue());
            update.setStatus(DataExportStatus.SUCCESS);
        } catch (Exception e) {
            log.error("导出失败", e);
            //导出失败
            update.setStatus(DataExportStatus.FAILED);
        } finally {
            if (Objects.nonNull(tempExcel)) {
                FileUtils.delete(tempExcel);
            }
            dataExportRecordRpcService.updateExportRecordData(update);
        }

    }

    /**
     * 上传文件到OSS
     * @param tempExcel
     * @return
     */
    private String uploadFileToOSS(File tempExcel) {
        try {
            Map<Integer, UploadParamDTO> uploadParamMap = Maps.newHashMapWithExpectedSize(
                    CommonPool.NUMBER_ONE);
            UploadParamDTO uploadParamDTO = new UploadParamDTO();
            try (InputStream inputStream = new FileInputStream(tempExcel)) {
                byte[] buffer = new byte[(int) tempExcel.length()];
                if (inputStream.read(buffer) != -1) {
                    uploadParamDTO.setFileBytes(buffer);
                    uploadParamDTO.setFormat(FileNameUtil.extName(tempExcel));
                } else {
                    throw new GlobalException("EXCEL 上传到OSS发生错误");
                }
            }
            uploadParamMap.put(CommonPool.NUMBER_ZERO, uploadParamDTO);
            Map<Integer, String> pathMap = pigeonChatStatisticsRpcService.batchUpload(
                    uploadParamMap);
            return pathMap.get(CommonPool.NUMBER_ZERO);
        } catch (Exception e) {
            log.error("上传失败:", e);
            throw new GlobalException("EXCEL 上传到OSS发生错误");
        }
    }

    /**
     *
     * @param tempExcel
     * @return
     */
    private int generateExcel(File tempExcel) {
        AtomicInteger counter = new AtomicInteger(CommonPool.NUMBER_ONE);
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.
                    write(tempExcel, RebateOrderExportDTO.class)
                    .withTemplate(new ClassPathResource(TEMP_FILE_PATH).getInputStream())
                    .needHead(Boolean.FALSE)
                    .build();
            WriteSheet writeSheet = EasyExcel.writerSheet(SHEET_NAME).build();

            while (true) {
                RebateOrderQueryDTO page = rebateOrderHandlerService.pageRebateOrder(queryDto);
                List<RebateOrder> records = page.getRecords();
                if (records.size() == CommonPool.NUMBER_ZERO) {
                    break;
                }
                queryDto.setCurrent(queryDto.getCurrent() + CommonPool.NUMBER_ONE);
                excelWriter.write(data(records, counter), writeSheet);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (Objects.nonNull(excelWriter)) {
                excelWriter.finish();
            }
        }
        return counter.get() - CommonPool.NUMBER_ONE;
    }

    /**
     * 列表数据转换成EXCEl对象数据
     * @param records
     * @param counter
     * @return
     */
    private List<RebateOrderExportDTO> data(List<RebateOrder> records, AtomicInteger counter) {
        List<RebateOrderExportDTO> list = Lists.newArrayListWithCapacity(records.size());
        Set<String> orderNos= Sets.newHashSetWithExpectedSize(records.size());
        Set<Long> buyerIds = Sets.newHashSetWithExpectedSize(records.size());
        Set<ShopProductKey> shopProductKeys= Sets.newHashSetWithExpectedSize(records.size());
        for (RebateOrder record : records) {
            orderNos.add(record.getOrderNo());
            buyerIds.add(record.getBuyerId());
            List<RebateOrderItem> rebateOrderItems = record.getRebateOrderItems();
            for (RebateOrderItem rebateOrderItem : rebateOrderItems) {
               ShopProductKey key=new ShopProductKey();
               key.setShopId(rebateOrderItem.getShopId());
               key.setProductId(rebateOrderItem.getProductId());
               shopProductKeys.add(key);
            }
        }
        List<OrderInfoDTO> orderInfoDTOS = orderRpcService.queryOrderInfoForExport(orderNos);
        Map<Long, UserInfoVO> userInfoVOMap = uaaRpcService.getUserDataBatchByUserIds(buyerIds);
        Map<ShopProductKey, Product> productMap = goodsRpcService.getProductListBatch(shopProductKeys);
        Map<String, OrderInfoDTO> orderInfoMap = orderInfoDTOS.stream().
                collect(Collectors.toMap(OrderInfoDTO::getOrderNo, Function.identity()));

        for (RebateOrder rebateOrder : records) {
            List<RebateOrderItem> rebateOrderItems = rebateOrder.getRebateOrderItems();
            for (RebateOrderItem rebateOrderItem : rebateOrderItems) {
                list.add(dealItem(rebateOrder,rebateOrderItem, counter,
                        orderInfoMap,userInfoVOMap,productMap));
            }

        }
        return list;
    }

    /**
     * 处理单条数据
     *
     * @param rebateOrder
     * @param counter
     * @param orderInfoDTOMap
     * @param userInfoVOMap
     * @param productMap
     * @return
     */
    private RebateOrderExportDTO dealItem(RebateOrder rebateOrder,RebateOrderItem rebateOrderItem,
            AtomicInteger counter,
            Map<String, OrderInfoDTO> orderInfoDTOMap, Map<Long, UserInfoVO> userInfoVOMap,
            Map<ShopProductKey, Product> productMap) {
        RebateOrderExportDTO dto=new RebateOrderExportDTO();
        dto.setNo(String.valueOf(counter.getAndIncrement()));
        dto.setOrderNo(rebateOrder.getOrderNo());
        OrderInfoDTO orderInfoDTO = orderInfoDTOMap.get(rebateOrder.getOrderNo());
        dto.setBuyerNickName(rebateOrder.getBuyerNickname());
        dto.setBuyerPhone(userInfoVOMap.getOrDefault(rebateOrder.getBuyerId(),new UserInfoVO()).getMobile());
        dto.setSpuId(rebateOrderItem.getProductId().toString());
        dto.setSpuName(rebateOrderItem.getProductName());
        dto.setSpuSource(dealSpuSource(productMap.get(new ShopProductKey(rebateOrderItem.getShopId(),rebateOrderItem.getProductId()))));
        if (Objects.nonNull(rebateOrderItem.getSpecs())) {
            dto.setSpec(String.join(",",rebateOrderItem.getSpecs()));
        }
        dto.setSkuId(rebateOrderItem.getSkuId().toString());
        //销售价
        dto.setSalePrice(dealPrice(rebateOrderItem.getSalePrice()));
        dto.setBuyNum(rebateOrderItem.getNum().toString());
        if (Objects.nonNull(orderInfoDTO)) {
            dto.setOrderStatus(orderInfoDTO.getOrderStatusContent());
            List<OrderDiscount> orderDiscounts = orderInfoDTO.getOrderDiscounts();
            dto.setStoreDiscount(calcDiscount(orderDiscounts,DiscountSourceType.SHOP_COUPON,rebateOrderItem));
            dto.setPlatformDiscount(calcDiscount(orderDiscounts,DiscountSourceType.PLATFORM_COUPON,rebateOrderItem));
            dto.setMemberDiscount(calcDiscount(orderDiscounts,DiscountSourceType.MEMBER_DEDUCTION,rebateOrderItem));
            //店铺总优惠 平台总优惠  后面可能要合并
            dto.setStoreTotalDiscount(dto.getStoreDiscount());
            dto.setPlatformTotalDiscount(dto.getPlatformDiscount());
            //付款时间
            if (Objects.nonNull(orderInfoDTO.getPayment())) {
                if (Objects.nonNull(orderInfoDTO.getPayment().getPayTime())) {
                    dto.setPayTime(orderInfoDTO.getPayment().getPayTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
                    dto.setPayType(dealPayType(orderInfoDTO.getPayment().getType()));
                }
                dto.setFreightPrice(dealPrice(orderInfoDTO.getPayment().getFreightAmount()));
            }else {
                dto.setPayTime(StringUtils.EMPTY);
                dto.setPayType(StringUtils.EMPTY);
                dto.setFreightPrice("0");
            }
        }else {
            dto.setOrderStatus("订单不存在");
            dto.setStoreDiscount("0");
            dto.setPlatformDiscount("0");
            dto.setMemberDiscount("0");
            dto.setStoreTotalDiscount("0");
            dto.setPlatformTotalDiscount("0");
            dto.setPayTime(StringUtils.EMPTY);
            dto.setPayType(StringUtils.EMPTY);
        }
        dto.setRebate(dealPrice(rebateOrderItem.getTotalRebate()));
        dto.setTotalPrice(dealPrice(rebateOrderItem.getDealPrice()*rebateOrderItem.getNum()));

        dto.setPayable(dealPrice(rebateOrderItem.getDealPrice()));
        dto.setPlatformServiceCharge(dealPrice(rebateOrderItem.getPlatformServiceFee()));
        dto.setExpectRebate(dealPrice(rebateOrderItem.getEstimatedRebate()));
        dto.setSettlementStatus(dealSettlementStatus(rebateOrderItem.getSettlementStatus()));
        dto.setPayStatus(dealPayStatus(rebateOrder.getStatus()));
        dto.setShopName(rebateOrder.getShopName());
        //下单时间
        dto.setOrderTime(rebateOrder.getOrderTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));


        return dto;
    }

    private String dealSettlementStatus(SettlementStatus settlementStatus) {
        switch (settlementStatus) {
            case PENDING_SETTLEMENT:
                return "待结算";
            case SETTLED:
                return "已结算";
            case EXPIRED:
                return "已失效";
            default:
                return StringUtils.EMPTY;
        }
    }

    private String dealPayStatus(RebateOrderStatus status) {
        switch (status) {
            case UNPAID:
                return "未付款";
            case PAID:
                return "已付款";
            case CLOSED:
                return "已关闭";
            case COMPLETED:
                return "已完成";
            default:
                return StringUtils.EMPTY;
        }
    }

    private String dealPayType(PayType type) {
        switch (type) {
            case ALIPAY:
                return "支付宝";
            case WECHAT:
                return "微信";
            case BALANCE:
                return "余额支付";
            default:
                return StringUtils.EMPTY;
        }
    }

    /**
     * 计算各个订单项的不同优惠种类金额
     * @param orderDiscounts
     * @param discountSourceType
     * @param rebateOrderItem
     * @return
     */
    private String calcDiscount(List<OrderDiscount> orderDiscounts,DiscountSourceType discountSourceType,RebateOrderItem rebateOrderItem){
        if (CollectionUtils.isEmpty(orderDiscounts)) {
           return String.valueOf(CommonPool.NUMBER_ZERO);
        }
        Map<DiscountSourceType, List<OrderDiscount>> sourceTypeMap = orderDiscounts.stream()
                .collect(Collectors.groupingBy(OrderDiscount::getSourceType));
        List<OrderDiscount> shopCouponDiscount = sourceTypeMap.get(discountSourceType);
        if (CollectionUtils.isEmpty(shopCouponDiscount)) {
            return String.valueOf(CommonPool.NUMBER_ZERO);
        }else {
            //订单项的优惠金额
            List<OrderDiscountItem> result = shopCouponDiscount.stream()
                    .flatMap(orderDiscount -> orderDiscount.getDiscountItems().stream())
                    .collect(Collectors.toList());

            Long totalDiscountAmount = result.stream()
                    .filter(item -> item.getItemId().equals(rebateOrderItem.getOrderItemId()))
                    .mapToLong(OrderDiscountItem::getDiscountAmount)
                    .sum();
            return BigDecimal.valueOf(totalDiscountAmount)
                    .divide(BigDecimal.valueOf(TEN_THOUSAND),
                            CommonPool.NUMBER_TWO, RoundingMode.HALF_UP)
                    .toPlainString();
        }


    }

    private String dealSpuSource(Product product) {
        if (Objects.isNull(product)) {
            return StringUtils.EMPTY;
        }
        return switch (product.getSellType()) {
            case OWN -> "自有";
            case PURCHASE -> "采购";
            case CONSIGNMENT -> "代销";
        };
    }

    /**
     * 生成临时文件
     * @return
     */
    private File generateTempExcelFile() {
        String tempPath = "/消费返利_%s.xlsx";
        String basePath = String.format(tempPath, IdUtil.fastUUID());
        File generateFile;
        try (InputStream templateInputStream = new ClassPathResource(TEMP_FILE_PATH).getInputStream()){
            // 复制模板文件到生成的临时文件
            generateFile = new File(basePath);
            Files.copy(templateInputStream, generateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("创建临时文件发生错误:", e);
            throw new GlobalException("创建临时文件发生错误");
        }
        return generateFile;
    }
    private String dealPrice(Long price) {
        return BigDecimal.valueOf(price).divide(BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_TEN_THOUSAND))
                .toString();
    }
}
