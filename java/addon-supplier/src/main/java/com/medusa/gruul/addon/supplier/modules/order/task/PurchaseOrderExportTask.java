package com.medusa.gruul.addon.supplier.modules.order.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.medusa.gruul.addon.supplier.model.bo.OrderExtra;
import com.medusa.gruul.addon.supplier.model.bo.OrderTimeNodes;
import com.medusa.gruul.addon.supplier.model.dto.OrderQueryPageDTO;
import com.medusa.gruul.addon.supplier.model.dto.PurchaseOrderExportDTO;
import com.medusa.gruul.addon.supplier.model.enums.OrderStatus;
import com.medusa.gruul.addon.supplier.model.enums.PackageStatus;
import com.medusa.gruul.addon.supplier.model.enums.SupplierError;
import com.medusa.gruul.addon.supplier.modules.goods.service.QuerySupplierGoodsService;
import com.medusa.gruul.addon.supplier.modules.order.service.SupplierOrderHandleService;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierGoods;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrder;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrderItem;
import com.medusa.gruul.carrier.pigeon.api.model.dto.UploadParamDTO;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.overview.api.enums.DataExportStatus;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.ClassPathResource;

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
import java.util.stream.Collectors;

/**
 * 采购订单导出任务
 *
 * @author jipeng
 * @since 2024/02/02
 */
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderExportTask implements Runnable {



    public  AtomicInteger INDEX_COUNTER = new AtomicInteger(CommonPool.NUMBER_ONE);
    /**
     * 一万
     */
    public static final Integer TEN_THOUSAND = 10000;
    /**
     * 模板文件路径
     */
    private static final String TEMP_FILE_PATH = "temp/采购订单_temp.xlsx";
    /**
     * SHEET名称
     */
    private static final String SHEET_NAME = "SheetJS";
    /**
     * 导出记录id
     */
    private final Long exportDataId;

    private final SupplierOrderHandleService supplierOrderHandleService;

    private final OrderQueryPageDTO queryPageDTO;

    private final UaaRpcService uaaRpcService;

    private final ShopRpcService shopRpcService;

    private final DataExportRecordRpcService dataExportRecordRpcService;

    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;

    private final QuerySupplierGoodsService querySupplierGoodsService;


    private static File generateTempExcelFile() {
        File generateFile;
        try (InputStream templateInputStream = new ClassPathResource(TEMP_FILE_PATH).getInputStream()) {
            // 复制资源文件到新的文件
            generateFile = new File("/采购订单_" + IdUtil.fastUUID() + ".xlsx");
            Files.copy(templateInputStream, generateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("创建临时文件失败", e);
            throw SupplierError.TEMP_PURCHASE_ORDER_GENERATE_ERROR.exception();
        }
        return generateFile;
    }

    @Override
    public void run() {

        File tempExcelFile = generateTempExcelFile();
        DataExportRecordDTO update = new DataExportRecordDTO();
        update.setId(exportDataId);

        try {
            int count = generateExcel(tempExcelFile);
            log.info("订单导出成功,数据条数为:{}", count);
            //上传文件到OSS
            String path = uploadFileToOSS(tempExcelFile);
            update.setFilePath(path);
            update.setDataCount(count);
            update.setFileSize(Long.valueOf(tempExcelFile.length()).intValue());
            update.setStatus(DataExportStatus.SUCCESS);
        } catch (Exception e) {
            log.error("订单导出失败", e);
            //导出失败
            update.setStatus(DataExportStatus.FAILED);
        } finally {
            if (Objects.nonNull(tempExcelFile)) {
                FileUtil.del(tempExcelFile);
            }
            dataExportRecordRpcService.updateExportRecordData(update);
        }

    }

    /**
     * 生成Excel
     *
     * @param tempExcelFile 导出数据存放的Excel文件
     * @return 生成的数据条数
     */
    private int generateExcel(File tempExcelFile) {

        ExcelWriter excelWriter = null;
        AtomicInteger counter = new AtomicInteger(CommonPool.NUMBER_ONE);
        try {
            excelWriter = EasyExcel.
                    write(tempExcelFile, PurchaseOrderExportDTO.class)
                    .registerWriteHandler(new CellMergeStrategy())
                    .withTemplate(new ClassPathResource(TEMP_FILE_PATH).getInputStream())
                    .needHead(Boolean.FALSE)
                    .build();
            WriteSheet writeSheet = EasyExcel.writerSheet(SHEET_NAME).build();
            while (Boolean.TRUE) {
                IPage<SupplierOrder> page = supplierOrderHandleService.orderPage(queryPageDTO);
                List<SupplierOrder> records = page.getRecords();
                if (records.size() == CommonPool.NUMBER_ZERO) {
                    break;
                }
                queryPageDTO.setCurrent(queryPageDTO.getCurrent() + CommonPool.NUMBER_ONE);
                //写入数据
                excelWriter.write(data(records, counter), writeSheet);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(excelWriter)) {
                excelWriter.close();
            }
        }
        return counter.get() - CommonPool.NUMBER_ONE;
    }

    /**
     * 数据写入excel前置处理
     *
     * @param records 数据记录
     * @param counter 计数器
     * @return 处理后的数据
     */
    private List<PurchaseOrderExportDTO> data(List<SupplierOrder> records, AtomicInteger counter) {
        List<PurchaseOrderExportDTO> result = Lists.newArrayListWithCapacity(records.size());
        Set<Long> userIds = records.stream().map(SupplierOrder::getShopUserId)
                .collect(Collectors.toSet());
        Set<Long> shopIds = records.stream().map(SupplierOrder::getShopId).collect(Collectors.toSet());
        Set<Long> productIds = records.stream()
                .flatMap(record -> record.getOrderItems().stream())
                .map(SupplierOrderItem::getProductId)
                .collect(Collectors.toSet());
        //供应商商品信息
        Map<Long, SupplierGoods> supplierProductMap = querySupplierGoodsService.getSupplierProductListByIds(
                productIds);
        List<ShopInfoVO> shopInfoVos = shopRpcService.getShopInfoByShopIdList(shopIds);
        Map<Long, UserInfoVO> userDataInfoMap = uaaRpcService.getUserDataBatchByUserIds(userIds);

        Map<Long, List<ShopInfoVO>> shopInfoMap = Maps.newHashMap();
        if (!CollectionUtil.isEmpty(shopInfoVos)) {
            shopInfoMap = shopInfoVos.stream().collect(Collectors.groupingBy(ShopInfoVO::getId));
        }

        for (SupplierOrder supplierOrder : records) {
            List<SupplierOrderItem> orderItems = supplierOrder.getOrderItems();
            if (CollectionUtil.isNotEmpty(orderItems)) {
                Boolean mergeFlag = Boolean.FALSE;
                Boolean increaseFlag = Boolean.FALSE;
                if (CommonPool.NUMBER_ONE == (orderItems.size())) {
                    increaseFlag = Boolean.TRUE;
                }
                for (int i = 0; i < orderItems.size(); i++) {
                    if (orderItems.size() > CommonPool.NUMBER_ONE &&
                            i == orderItems.size() - CommonPool.NUMBER_ONE) {
                        // 属于最后一个订单项
                        mergeFlag = Boolean.TRUE;
                        increaseFlag = Boolean.TRUE;
                    }
                    PurchaseOrderExportDTO dto = dealItem(counter, supplierOrder, orderItems.get(i),
                            userDataInfoMap, shopInfoMap,
                            mergeFlag, increaseFlag,supplierProductMap);
                    result.add(dto);
                }
            }
        }

        return result;
    }

    /**
     * 处理每一条数据
     * @param counter
     * @param supplierOrder
     * @param orderItem
     * @param userInfoVOMap
     * @param shopInfoMap
     * @param mergeFlag
     * @param increaseFlag
     * @return
     */
    private PurchaseOrderExportDTO dealItem(AtomicInteger counter, SupplierOrder supplierOrder,
                                            SupplierOrderItem orderItem, Map<Long, UserInfoVO> userInfoVOMap,
                                            Map<Long, List<ShopInfoVO>> shopInfoMap, Boolean mergeFlag, Boolean increaseFlag,
            Map<Long, SupplierGoods> supplierGoodsMap) {
        PurchaseOrderExportDTO dto = new PurchaseOrderExportDTO();
        int indexCount = INDEX_COUNTER.getAndIncrement();
        //编号
        int index = increaseFlag ? counter.getAndIncrement() : counter.get();
        dto.setIndex(String.valueOf(index));
        if (mergeFlag) {
            CellMergeStrategy.MERGE_INDEX_THREAD_LOCAL.get()
                    .put(indexCount, supplierOrder.getOrderItems().size() - 1);
        }
        //订单编号
        dto.setOrderNo(supplierOrder.getNo());
        //商品编号
        dto.setProductId(String.valueOf(orderItem.getProductId()));
        //订单状态
        dto.setStatus(dealOrderStatus(supplierOrder));
        //商品名称
        dto.setProductName(orderItem.getProductName());
        //商品来源
//        dto.setProductSource(dealProductSource(supplierOrder));
        //商品类型
        dto.setProductType(dealProductType(supplierGoodsMap.get(orderItem.getProductId())));
        //规格编号(SKUID)
        dto.setSukId(String.valueOf(orderItem.getSkuId()));
        //规格
        dto.setProductSpec(CollectionUtil.isEmpty(orderItem.getSpecs()) ? StringUtils.EMPTY :
                Joiner.on(",").join(orderItem.getSpecs()));
        //采购单价
        dto.setProductPrice(
                BigDecimal.valueOf(orderItem.getDealPrice()).divide(BigDecimal.valueOf(10000))
                        .toString());
        //采购数量
        dto.setProductNum(String.valueOf(orderItem.getNum()));
        //待发货
        dto.setWaitDeliveryNum(
                String.valueOf(PackageStatus.WAITING_FOR_DELIVER.equals(orderItem.getPackageStatus())
                        ? orderItem.getNum() : CommonPool.NUMBER_ZERO));
        //已发货数
        dto.setDeliveryNum(String.valueOf(
                PackageStatus.WAITING_FOR_RECEIVE.equals(orderItem.getPackageStatus())
                        ? orderItem.getNum() : CommonPool.NUMBER_ZERO));
        //已入库数
        dto.setStorageNum(String.valueOf(
                PackageStatus.COMPLETED.equals(orderItem.getPackageStatus())
                        ? orderItem.getNum() : CommonPool.NUMBER_ZERO));
        //剩余入库
        dto.setWaitStorageNum(String.valueOf(
                PackageStatus.COMPLETED.equals(orderItem.getPackageStatus())
                        ? CommonPool.NUMBER_ZERO : orderItem.getNum()));
        //商品总价
        dto.setProductTotalPrice(dealTotalPrice(supplierOrder));
        //运费
        dto.setFreight(dealFreight(supplierOrder));
        //应付款
        dto.setPayment(dealPrice(supplierOrder.getPayAmount()));
        //支付状态
        OrderTimeNodes timeNodes = supplierOrder.getTimeNodes();
        if (Objects.nonNull(timeNodes) && Objects.nonNull(timeNodes.getPayTime())) {
            dto.setPaymentStatus("已支付");
            //支付时间
            dto.setPaymentTime(
                    timeNodes.getPayTime()
                            .format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        } else {
            dto.setPaymentStatus("待支付");
        }

        OrderExtra extra = supplierOrder.getExtra();
        //买家昵称
        Option.of(userInfoVOMap.get(supplierOrder.getShopUserId())).peek(userInfoVO -> {
            //买家昵称
            dto.setBuyerNickName(userInfoVO.getNickname());
            //买家手机号
            dto.setBuyerPhone(userInfoVO.getMobile());
        });
        //收货人姓名 收货人电话 收货地址
        Option.of(extra.getReceiver()).peek(receiver -> {
            dto.setConsigneeName(receiver.getName());
            dto.setConsigneePhone(receiver.getMobile());
            dto.setConsigneeAddress(Joiner.on("").join(receiver.getArea())+receiver.getAddress());
        });
        //下单时间
        dto.setCreateTime(
                supplierOrder.getCreateTime()
                        .format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        //所属店铺
        Option.of(shopInfoMap.get(supplierOrder.getShopId())).peek(
                shopInfoVOS -> dto.setShopName(shopInfoVOS.get(0).getName()));
        //支付方式
        Option.of(extra.getPay()).peek(orderPayDTO -> {
                    switch (orderPayDTO.getPayType()) {
                        case OFFLINE:
                            dto.setPaymentType("线下支付");
                            break;
                        case BALANCE:
                            dto.setPaymentType("余额支付");
                            break;
                        default:
                            dto.setPaymentType(StringUtils.EMPTY);
                            break;
                    }
                }

        );

        //供应商
        dto.setSupplier(supplierOrder.getExtraInfo().getString("supplierName"));
        //供应商手机号
        dto.setSupplierPhone(supplierOrder.getExtraInfo().getString("supplierPhone"));
        //联系电话
        List<ShopInfoVO> shopInfoVOS = shopInfoMap.get(supplierOrder.getShopId());
        if (Objects.nonNull(shopInfoVOS)) {
            dto.setConcatPhone(shopInfoVOS.get(0).getContractNumber());
        }else {
            dto.setConcatPhone(StringUtils.EMPTY);
        }
        return dto;
    }

    /**
     * 商品类型
     * @param supplierGoods
     * @return
     */
    private String dealProductType(SupplierGoods supplierGoods) {
        if (Objects.isNull(supplierGoods)) {
            return StringUtils.EMPTY;
        }
        switch (supplierGoods.getProductType()) {
            case REAL_PRODUCT:
                return "实物商品（快递/同城/自提）";
            case VIRTUAL_PRODUCT:
                return "虚拟商品（无需物流）";
            default:
                return StringUtils.EMPTY;
        }
    }

    /**
     * 文件上传到OSS
     *
     * @param tempExcelFile 上传的文件
     * @return 文件上传后的路径
     */
    private String uploadFileToOSS(File tempExcelFile) {
        try {
            Map<Integer, UploadParamDTO> uploadParamMap = Maps.newHashMapWithExpectedSize(
                    CommonPool.NUMBER_ONE);
            UploadParamDTO uploadParamDTO = new UploadParamDTO();
            try (InputStream inputStream = new FileInputStream(tempExcelFile)) {
                byte[] buffer = new byte[(int) tempExcelFile.length()];
                inputStream.read(buffer);
                uploadParamDTO.setFileBytes(buffer);
                uploadParamDTO.setFormat(FileNameUtil.extName(tempExcelFile));
            }
            uploadParamMap.put(CommonPool.NUMBER_ZERO, uploadParamDTO);
            Map<Integer, String> pathMap = pigeonChatStatisticsRpcService.batchUpload(
                    uploadParamMap);
            return pathMap.get(CommonPool.NUMBER_ZERO);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw SupplierError.PURCHASE_ORDER_UPLOADER_ERROR.exception();
        }
    }

    /**
     * 运费
     *
     * @param supplierOrder 供应商订单实体类
     * @return 运费处理后的文案
     */
    private String dealFreight(SupplierOrder supplierOrder) {
        List<SupplierOrderItem> orderItems = supplierOrder.getOrderItems();
        if (CollectionUtil.isEmpty(orderItems)) {
            return String.valueOf(CommonPool.NUMBER_ZERO);
        }
        Long totalFreighPrice = orderItems.stream().map(SupplierOrderItem::getFreightPrice)
                .reduce(Long.valueOf(CommonPool.NUMBER_ZERO), Long::sum);
        return dealPrice(totalFreighPrice);

    }

    private String dealPrice(Long price) {
        return BigDecimal.valueOf(price).divide(BigDecimal.valueOf(TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    /**
     * 商品总价
     *
     * @param supplierOrder 供应商订单
     * @return 总价处理后的文案
     */
    private String dealTotalPrice(SupplierOrder supplierOrder) {
        List<SupplierOrderItem> orderItems = supplierOrder.getOrderItems();
        if (CollectionUtil.isEmpty(orderItems)) {
            return StringUtils.EMPTY;
        }
        Long total = orderItems.stream().map(x -> x.getSalePrice() * x.getNum())
                .reduce(Long.valueOf(CommonPool.NUMBER_ZERO), Long::sum);
        return dealPrice(total);

    }



    /**
     * 订单状态
     *
     * @param supplierOrder
     * @return 订单状态文案
     */
    private String dealOrderStatus(SupplierOrder supplierOrder) {
        OrderStatus status = supplierOrder.getStatus();
        if (OrderStatus.UNPAID.equals(status)) {
            return PayStatusConst.UNPAID;
        }
        if (OrderStatus.PAYMENT_AUDIT.equals(status)) {
            return PayStatusConst.PAYMENT_AUDIT;
        }
        //已关闭
        if (Set.of(OrderStatus.AUDIT_FAIL_CLOSED, OrderStatus.SYSTEM_CLOSED,
                OrderStatus.SELLER_CLOSED, OrderStatus.BUYER_CLOSED).contains(status)) {
            return PayStatusConst.CLOSED;
        }
        if (OrderStatus.PAID.equals(status)) {
            List<SupplierOrderItem> orderItems = supplierOrder.getOrderItems();
            if (CollectionUtil.isNotEmpty(orderItems)) {
                Map<PackageStatus, List<SupplierOrderItem>> collect = orderItems.stream()
                        .collect(Collectors.groupingBy(SupplierOrderItem::getPackageStatus));
                //待发货
                Boolean deliverFlag = Boolean.FALSE;
                Boolean receiveFlag = Boolean.FALSE;
                List<SupplierOrderItem> items = collect.getOrDefault(PackageStatus.WAITING_FOR_DELIVER,
                        Lists.newArrayList());
                if (items.size() > CommonPool.NUMBER_ZERO) {
                    deliverFlag = Boolean.TRUE;
//
                }
                //待入库
                items = collect.getOrDefault(PackageStatus.WAITING_FOR_RECEIVE, Lists.newArrayList());
                if (items.size() > CommonPool.NUMBER_ZERO) {
                    receiveFlag = Boolean.TRUE;

                }
                if (deliverFlag && receiveFlag) {
                    //既有待发货 也有待收货
                    return PayStatusConst.PART_SEND;
                }
                if (deliverFlag && !receiveFlag) {
                    //只有待发货
                    return PayStatusConst.WAITING_FOR_DELIVER;
                }
                if (receiveFlag && !deliverFlag) {
                    //只有待收货
                    return PayStatusConst.WAITING_FOR_RECEIVE;
                }
                //已完成
                items = collect.getOrDefault(PackageStatus.COMPLETED, Lists.newArrayList());
                if (items.size() > CommonPool.NUMBER_ZERO) {
                    return PayStatusConst.FINISHED;
                }


            }
        }
        return StringUtils.EMPTY;
    }

    interface PayStatusConst {

        String UNPAID = "待支付";
        String PAYMENT_AUDIT = "待审核";
        String WAITING_FOR_DELIVER = "待发货";

        String PART_SEND = "部分发货";
        String WAITING_FOR_RECEIVE = "待入库";
        String FINISHED = "已完成";

        String CLOSED = "已关闭";

    }

    /**
     * 单元格合并策略
     */
    public static class CellMergeStrategy extends AbstractMergeStrategy {

        //保存需要合并单元格的索引
        public static final ThreadLocal<Map<Integer, Integer>> MERGE_INDEX_THREAD_LOCAL =
                ThreadLocal.withInitial(Maps::newHashMap);

        @Override
        protected void merge(Sheet sheet, Cell cell, Head head, Integer integer) {
            //合并单元格开始列索引
            int firstMergeColumnIndex = 14;
            List<Integer> mergeIndexList = Lists.newArrayList(0, 1);
            //获取行号列号
            int rowNum = cell.getRowIndex();
            int columnNum = cell.getColumnIndex();
            // 3、获取第一个Cell中的值 其中记录索引号 用于判断是否需要合并
            if (rowNum >= 1 && (columnNum >= firstMergeColumnIndex || mergeIndexList.contains(
                    columnNum))) {
                if (MERGE_INDEX_THREAD_LOCAL.get()
                        .containsKey(rowNum)) {
                    int startRowNum = rowNum - MERGE_INDEX_THREAD_LOCAL.get().get(rowNum);
                    CellRangeAddress cellAddresses =
                            new CellRangeAddress(startRowNum,
                                    rowNum, columnNum,
                                    columnNum);
                    // 3、修改合并单元格的样式
                    sheet.addMergedRegion(cellAddresses);
                    Cell cell2 = sheet.getRow(rowNum - 1).getCell(columnNum);
                    CellStyle cellStyle = cell2.getCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    cell2.setCellStyle(cellStyle);


                }

            }


        }


    }


}
