package com.medusa.gruul.order.service.modules.order.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.generator.UUIDGenerator;
import cn.hutool.json.JSONObject;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.medusa.gruul.carrier.pigeon.api.model.dto.UploadParamDTO;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.api.enums.DiscountSourceType;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.helper.OrderHelper;
import com.medusa.gruul.order.service.model.dto.OrderExportDTO;
import com.medusa.gruul.order.service.model.dto.OrderQueryDTO;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.modules.order.service.QueryOrderCallShopRpcService;
import com.medusa.gruul.order.service.mp.service.IOrderDiscountService;
import com.medusa.gruul.overview.api.enums.DataExportStatus;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 订单导出任务
 *
 * @author jipeng
 * @since 2024-01-09
 */

@RequiredArgsConstructor
@Slf4j
public class OrderExportTask implements Runnable {

    public static final String SHEET_NAME = "SheetJS";
    /**
     * 一万
     */
    public static final Integer TEN_THOUSAND = 10000;
    public static final String EMPTY = "";
    private static final String TEMP_FILE_PATH = "temp/用户订单_temp.xlsx";
    private final static String TEMP_PATH = "/用户订单_%s.xlsx";
    /**
     * 订单生成记录id
     */
    private final Long exportRecordId;
    private final DataExportRecordRpcService dataExportRecordRpcService;
    private final QueryOrderCallShopRpcService queryOrderCallShopRpcService;
    private final OrderQueryDTO queryPage;
    private final UaaRpcService uaaRpcService;
    private final IOrderDiscountService orderDiscountService;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final UUIDGenerator uuidGenerator = new UUIDGenerator();
    private final AtomicInteger indexCounter = new AtomicInteger(CommonPool.NUMBER_ONE);
    private final ThreadLocal<Map<Long, UserInfoVO>> USER_PHONE_INFO_THREAD_LOCAL = new ThreadLocal<>();

    public static String getPath() {
        return Objects.requireNonNull(OrderExportDTO.class.getResource("/")).getPath();
    }

    @SneakyThrows
    @Override
    public void run() {

        File generateFile = null;
        try (InputStream templateInputStream = new ClassPathResource(TEMP_FILE_PATH).getInputStream()) {
            // 复制资源文件到新的文件
            generateFile = new File(String.format(TEMP_PATH, uuidGenerator.next()));
            Files.copy(templateInputStream, generateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("创建临时文件失败", e);
        }

        DataExportRecordDTO update = new DataExportRecordDTO();
        update.setId(exportRecordId);
        try {
            int count = generateExcel(generateFile);
            log.debug("订单导出成功,数据条数为:{}", count);
            //上传文件到OSS
            String path = uploadFileToOSS(generateFile);
            update.setFilePath(path);
            update.setDataCount(count);
            update.setFileSize(Long.valueOf(generateFile != null ? generateFile.length() : 0).intValue());
            update.setStatus(DataExportStatus.SUCCESS);
        } catch (Exception e) {
            log.error("订单导出失败", e);
            //导出失败
            update.setStatus(DataExportStatus.FAILED);
        } finally {
            if (Objects.nonNull(generateFile)) {
                FileUtils.delete(generateFile);
            }
            dataExportRecordRpcService.updateExportRecordData(update);
        }


    }

    /**
     * 上传文件到OSS
     *
     * @param tempExcelFile 临时文件
     * @return 文件路径
     */
    private String uploadFileToOSS(File tempExcelFile) {
        try {
            Map<Integer, UploadParamDTO> uploadParamMap = Maps.newHashMapWithExpectedSize(CommonPool.NUMBER_ONE);
            UploadParamDTO uploadParamDTO = new UploadParamDTO();
            try (InputStream inputStream = new FileInputStream(tempExcelFile)) {
                byte[] buffer = new byte[(int) tempExcelFile.length()];
                if (inputStream.read(buffer) != -1) {
                    uploadParamDTO.setFileBytes(buffer);
                    uploadParamDTO.setFormat(FileNameUtil.extName(tempExcelFile));
                } else {
                    throw OrderError.USER_ORDER_UPLOADER_ERROR.exception();
                }
            }
            uploadParamMap.put(CommonPool.NUMBER_ZERO, uploadParamDTO);
            Map<Integer, String> pathMap = pigeonChatStatisticsRpcService.batchUpload(uploadParamMap);
            return pathMap.get(CommonPool.NUMBER_ZERO);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw OrderError.USER_ORDER_UPLOADER_ERROR.exception();
        }
    }

    /**
     * 导出Excel文件
     *
     * @param generateExcel 导出的文件
     * @return 记录的数量
     */
    private Integer generateExcel(File generateExcel) {
        ExcelWriter excelWriter = null;
        AtomicInteger counter = new AtomicInteger(CommonPool.NUMBER_ONE);

        try {
            excelWriter = EasyExcel.write(generateExcel, OrderExportDTO.class).registerWriteHandler(new CellStyleStrategy()).withTemplate(new ClassPathResource(TEMP_FILE_PATH).getInputStream()).needHead(Boolean.FALSE).build();
            WriteSheet writeSheet = EasyExcel.writerSheet(SHEET_NAME).build();
            Set<String> exportShopOrderNos = queryPage.getExportShopOrderNos();
            if (CollUtil.isNotEmpty(exportShopOrderNos)) {
                queryPage.setExportOrderNos(
                        exportShopOrderNos.stream()
                                .map(OrderHelper::shopOrderNoToOrderNo)
                                .collect(Collectors.toSet())
                );
            }

            while (true) {
                IPage<Order> orderPage = queryOrderCallShopRpcService.orderPage(true, queryPage);
                List<Order> records = orderPage.getRecords();
                if (records.isEmpty()) {
                    break;
                }
                if (CollUtil.isNotEmpty(exportShopOrderNos)) {
                    for (Order record : records) {
                        //根据订单号过滤订单
                        if (Objects.nonNull(record.getShopOrders())) {
                            List<ShopOrder> shopOrders = record.getShopOrders().stream().filter(x -> exportShopOrderNos.contains(x.getNo())).collect(Collectors.toList());
                            record.setShopOrders(shopOrders);
                        }
                    }
                }
                //用户ids
                Set<Long> userIds = records.stream().map(Order::getBuyerId).collect(Collectors.toSet());
                //查询订单折扣优惠券
                Set<String> orderNoIds = records.stream().map(Order::getNo).collect(Collectors.toSet());
                List<OrderDiscount> orderDiscounts = orderDiscountService.orderDiscountsByOrderNos(Lists.newArrayList(orderNoIds));
                if (CollUtil.isNotEmpty(orderDiscounts)) {
                    Map<String, List<OrderDiscount>> orderDisCountMap = orderDiscounts.stream().collect(Collectors.groupingBy(OrderDiscount::getOrderNo));
                    records = records.stream().peek(x -> x.setOrderDiscounts(orderDisCountMap.getOrDefault(x.getNo(), Lists.newArrayList()))).collect(Collectors.toList());
                }
                USER_PHONE_INFO_THREAD_LOCAL.set(uaaRpcService.getUserDataBatchByUserIds(userIds));
                queryPage.setCurrent(queryPage.getCurrent() + CommonPool.NUMBER_ONE);

                excelWriter.write(data(records, counter), writeSheet);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            USER_PHONE_INFO_THREAD_LOCAL.remove();
            if (Objects.nonNull(excelWriter)) {
                excelWriter.close();
            }
        }

        return counter.get() - 1;
    }

    /**
     * 记录数据的渲染
     *
     * @param records 需要导出的数据
     * @param index   索引号
     * @return 渲染数据
     */
    public List<OrderExportDTO> data(List<Order> records, AtomicInteger index) {
        List<OrderExportDTO> result = Lists.newArrayList();
        //循环订单
        for (Order order : records) {
            List<ShopOrder> shopOrders = order.getShopOrders();
            if (CollUtil.isNotEmpty(shopOrders)) {
                //循环店铺订单
                for (ShopOrder shopOrder : shopOrders) {
                    List<ShopOrderItem> shopOrderItems = shopOrder.getShopOrderItems();
                    if (CollUtil.isNotEmpty(shopOrderItems)) {
                        //循环商品
                        //对商品进行SKU分组
                        Map<Long, List<ShopOrderItem>> collect = shopOrderItems.stream().collect(Collectors.groupingBy(ShopOrderItem::getSkuId));
                        shopOrder.setSkuGroupShopOrderItems(collect);
                        //对店铺订单最后一个商品进行单元格合并标记
                        int count = CommonPool.NUMBER_ONE;
                        Boolean mergeFlag = Boolean.FALSE;
                        Boolean increaseIndexFlag = Boolean.FALSE;
                        if (CommonPool.NUMBER_ONE == (collect.size())) {
                            increaseIndexFlag = Boolean.TRUE;
                        }
                        //根据sku遍历
                        for (Entry<Long, List<ShopOrderItem>> entry : collect.entrySet()) {
                            if (count != 1 && count == collect.size()) {
                                mergeFlag = Boolean.TRUE;
                                increaseIndexFlag = Boolean.TRUE;
                            }
                            OrderExportDTO dto = renderDealItem(order, shopOrder, entry.getValue(), index, mergeFlag, increaseIndexFlag);
                            count++;
                            result.add(dto);
                        }
                    }
                }
            }


        }
        return result;
    }

    /**
     * 单条数据渲染
     *
     * @param order             订单
     * @param shopOrder         店铺订单
     * @param shopOrderItems    订单项
     * @param index             索引
     * @param mergeFlag         是否合并的标记为
     * @param increaseIndexFlag 编号增加的标记位
     * @return 单条数据
     */
    private OrderExportDTO renderDealItem(Order order, ShopOrder shopOrder, List<ShopOrderItem> shopOrderItems, AtomicInteger index, Boolean mergeFlag, Boolean increaseIndexFlag) {
        ShopOrderItem firstShopItem = shopOrderItems.get(0);

        OrderExportDTO dto = new OrderExportDTO();

        int indexCount = indexCounter.getAndIncrement();
        //编号
        int indexNo = increaseIndexFlag ? index.getAndIncrement() : index.get();
        dto.setNo(String.valueOf(indexNo));
        if (mergeFlag) {
            CellStyleStrategy.MERGE_INDEX_THREAD_LOCAL.get().put(indexCount, shopOrder.getSkuGroupShopOrderItems().size() - 1);
        }

        //订单编号
        dto.setOrderNo(order.getNo());
        //订单状态
        dto.setOrderStatus(transOrderStatus(order));
        //买家昵称
        dto.setBuyerNickName(order.getBuyerNickname());
        //买家手机号
        UserInfoVO userInfoVO = USER_PHONE_INFO_THREAD_LOCAL.get().get(order.getBuyerId());
        dto.setBuyerPhone(Option.of(userInfoVO).map(UserInfoVO::getMobile).getOrElse(EMPTY));
        //商品名称
        dto.setProductName(firstShopItem.getProductName());
        //商品ID
        dto.setProductId(firstShopItem.getProductId().toString());
        //商品来源
        dto.setProductSource(dealProductSource(firstShopItem));
        //商品规格
        if (CollUtil.isNotEmpty(firstShopItem.getSpecs())) {

            dto.setProductSpec(Joiner.on(",").join(firstShopItem.getSpecs()));
        }
        //规格id
        dto.setSkuId(firstShopItem.getSkuId().toString());
        //销售价
        dto.setProductSellPrice(BigDecimal.valueOf(firstShopItem.getSalePrice()).divide(BigDecimal.valueOf(TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP).toString());
        //购买数量
        dto.setProductNum(shopOrderItems.stream().map(ShopOrderItem::getNum).reduce(CommonPool.NUMBER_ZERO, Integer::sum).toString());
        //已发货数
        dto.setProductSendNum(shopOrderItems.stream().filter(x -> {
            if (Objects.nonNull(x.getPackageStatus()) && !PackageStatus.WAITING_FOR_DELIVER.equals(x.getPackageStatus())) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }).map(ShopOrderItem::getNum).reduce(CommonPool.NUMBER_ZERO, Integer::sum).toString());
        //待发货数、
        dto.setProductWaitSendNum(shopOrderItems.stream().filter(x -> {
            if (Objects.nonNull(x.getPackageStatus()) && PackageStatus.WAITING_FOR_DELIVER.equals(x.getPackageStatus())) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }).map(ShopOrderItem::getNum).reduce(CommonPool.NUMBER_ZERO, Integer::sum).toString());
        List<OrderDiscount> orderDiscounts = order.getOrderDiscounts();
        if (Objects.nonNull(orderDiscounts)) {
            //会员折扣、
            dto.setProductMemberDiscount(calcDiscount(orderDiscounts, DiscountSourceType.MEMBER_DEDUCTION ,firstShopItem));
            //消费返利、
            dto.setProductConsumeRefund(calcDiscount(orderDiscounts, DiscountSourceType.CONSUMPTION_REBATE ,firstShopItem));
            //店铺优惠、
            dto.setProductShopDiscount(calcDiscount(orderDiscounts, DiscountSourceType.SHOP_COUPON,firstShopItem));
            //平台优惠
            dto.setProductPlatformDiscount(calcDiscount(orderDiscounts, DiscountSourceType.PLATFORM_COUPON,firstShopItem));
        } else {
            dto.setProductMemberDiscount(BigDecimal.valueOf(0.00).toString());
            //消费返利、
            dto.setProductConsumeRefund(BigDecimal.valueOf(0.00).toString());
            //店铺优惠、
            dto.setProductShopDiscount(BigDecimal.valueOf(0.00).toString());
            //平台优惠
            dto.setProductPlatformDiscount(BigDecimal.valueOf(0.00).toString());
        }

        //合并单元格
        OrderPayment orderPayment = order.getOrderPayment();
        if (Objects.nonNull(orderPayment)) {
            //商品总价
            dto.setProductTotalPrice(dealTotalPrice(shopOrder));
            //应付款
            dto.setProductPayPrice(BigDecimal.valueOf(orderPayment.getPayAmount()).divide(BigDecimal.valueOf(TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP).toString());
            //运费、
            dto.setProductFreightPrice(BigDecimal.valueOf(orderPayment.getFreightAmount()).divide(BigDecimal.valueOf(TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP).toString());
            //平台总优惠、
            dto.setProductPlatformTotalDiscount(BigDecimal.valueOf(orderPayment.getDiscountAmount()).divide(BigDecimal.valueOf(TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP).toString());
            //应付款
            dto.setProductPayPrice(BigDecimal.valueOf(orderPayment.getPayAmount()).divide(BigDecimal.valueOf(TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP).toString());
            //支付状态
            dto.setProductPayStatus(Objects.nonNull(orderPayment.getPayTime()) ? "已支付" : "待支付");
            //支付方式
            dto.setProductPayType(payTypeTrans(orderPayment.getType()));
            if (Objects.nonNull(orderPayment.getPayTime())) {
                //支付时间
                dto.setProductPayTime(orderPayment.getPayTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            }
            //平台总优惠
            dto.setProductPlatformTotalDiscount(BigDecimal.valueOf(orderPayment.getDiscountAmount()).divide(BigDecimal.valueOf(TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP).toString());
            //店铺总优惠
            dto.setProductShopTotalDiscount(calcShopDiscount(orderPayment));

        }

        //配送方式、
        dto.setProductDeliveryType(deliveryTypeTrans(order));

        dto.setProductChannel(dealPlatform(order.getPlatform()));
        OrderReceiver orderReceiver = order.getOrderReceiver();
        if (Objects.nonNull(orderReceiver)) {
            //收货人姓名
            dto.setProductReceiverName(orderReceiver.getName());
            //收货人电话
            dto.setProductReceiverPhone(orderReceiver.getMobile());
            //收货地址Joiner.on("").join(orderReceiver.getArea())+orderReceiver.getAddress()
            dto.setProductReceiverAddress(Joiner.on("").join(orderReceiver.getArea())+orderReceiver.getAddress());
        }
        //所属店铺
        dto.setProductShopName(shopOrder.getShopName());

        //下单时间
        dto.setProductCreateTime(shopOrder.getCreateTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        //用户备注
        dto.setProductRemark(order.getRemark());
        return dto;
    }

    /**
     * 计算店铺订单商品总价
     *
     * @param shopOrder 店铺订单
     * @return 店铺订单商品总价
     */
    private String dealTotalPrice(ShopOrder shopOrder) {
        Long sum = 0L;
        List<ShopOrderItem> shopOrderItems = shopOrder.getShopOrderItems();
        for (ShopOrderItem shopOrderItem : shopOrderItems) {
            sum += shopOrderItem.getDealPrice() * shopOrderItem.getNum();
            sum += shopOrderItem.getFixPrice();
            sum += shopOrderItem.getFreightPrice();
        }
        return dealPrice(sum);
    }

    private String dealPrice(Long price) {
        return BigDecimal.valueOf(price).divide(BigDecimal.valueOf(TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private String dealPlatform(Platform platform) {
        if (Objects.isNull(platform)) {
            return "未知";
        }
        return switch (platform) {
            case WECHAT_MINI_APP -> "微信小程序";
            case WECHAT_MP -> "微信公众号";
            case PC -> "PC端";
            case H5 -> "移动端h5";
            case IOS -> "苹果";
            case ANDROID -> "安卓";
            case HARMONY -> "鸿蒙";
        };
    }

    /**
     * 折扣
     *
     * @param orderDiscounts   订单优惠
     * @param firstShopItem 订单项
     * @return 折扣
     */
    private String calcDiscount(List<OrderDiscount> orderDiscounts, DiscountSourceType discountSourceType,
            ShopOrderItem firstShopItem) {
        List<OrderDiscount> memberOrderDiscount = orderDiscounts.stream()
                .filter(x -> discountSourceType.equals(x.getSourceType()))
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(memberOrderDiscount)) {
            List<OrderDiscountItem> totalItems = Lists.newArrayList();
            for (OrderDiscount orderDiscount : memberOrderDiscount) {
                List<OrderDiscountItem> discountItems = orderDiscount.getDiscountItems();
                if (CollUtil.isNotEmpty(discountItems)) {
                    totalItems.addAll(discountItems);
                }
                //获取指定商品的会有优惠金额
                Long sum = totalItems.stream()
                        .filter(x -> x.getItemId().equals(firstShopItem.getId()))
                        .map(OrderDiscountItem::getDiscountAmount)
                        .reduce((long) CommonPool.NUMBER_ZERO, Long::sum);

                return BigDecimal.valueOf(sum)
                        .divide(BigDecimal.valueOf(TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP)
                        .toPlainString();
            }
        }
        return BigDecimal.valueOf(0.00).toString();
    }

    private String dealProductSource(ShopOrderItem shopOrderItem) {
        if (Objects.isNull(shopOrderItem.getSellType())) {
            return EMPTY;
        }
        return switch (shopOrderItem.getSellType()) {
            case PURCHASE -> "采购商品";
            case CONSIGNMENT -> "代销商品";
            case OWN -> "自有商品";
        };
    }

    /**
     * TODO 代码需要优化
     * 订单状态
     *
     * @param order 订单
     * @return 订单状态
     */
    public static String transOrderStatus(Order order) {
        OrderStatus status = order.getStatus();
        if (OrderType.TEAM.equals(order.getType())) {
            if (OrderStatus.TEAMING.equals(order.getStatus())) {
                return "拼团中";
            }
            if (OrderStatus.TEAM_FAIL.equals(order.getStatus())) {
                return "拼团失败";
            }
        }
        if (OrderStatus.UNPAID.equals(status)) {
            return "待付款";
        }
        if (OrderStatus.SYSTEM_CLOSED.equals(status) || OrderStatus.BUYER_CLOSED.equals(status) || OrderStatus.SELLER_CLOSED.equals(status)) {
            return "已关闭";
        }
        if (OrderStatus.PAID.equals(status) || OrderStatus.TEAMING.equals(status) || OrderStatus.TEAM_FAIL.equals(status)) {
            //待发货状态
            boolean waitForSendFlag = Boolean.FALSE;
            //待收货状态
            boolean waitForReceiveFlag = Boolean.FALSE;
            //订单已付款 订单是PAID状态 订单项的状态是OK 但是包裹id为空
            List<ShopOrder> shopOrders = order.getShopOrders();
            for (ShopOrder shopOrder : shopOrders) {
                List<ShopOrderItem> shopOrderItems = shopOrder.getShopOrderItems();
                for (ShopOrderItem shopOrderItem : shopOrderItems) {
                    if (ItemStatus.OK.equals(shopOrderItem.getStatus())) {

                        if (Objects.isNull(shopOrderItem.getPackageId())) {
                            //待发货状态
                            waitForSendFlag = Boolean.TRUE;
                        }
                        if (PackageStatus.WAITING_FOR_RECEIVE.equals(shopOrderItem.getPackageStatus())) {
                            waitForReceiveFlag = Boolean.TRUE;
                        }
                        if (PackageStatus.BUYER_WAITING_FOR_COMMENT.equals(shopOrderItem.getPackageStatus()) || PackageStatus.SYSTEM_WAITING_FOR_COMMENT.equals(shopOrderItem.getPackageStatus())) {
                            return "待评价";
                        }
                        if (PackageStatus.BUYER_COMMENTED_COMPLETED.equals(shopOrderItem.getPackageStatus()) || PackageStatus.SYSTEM_COMMENTED_COMPLETED.equals(shopOrderItem.getPackageStatus())) {
                            //买家已评论 或者系统自动好评
                            return "已完成";
                        }
                    }
                    if (ItemStatus.CLOSED.equals(shopOrderItem.getStatus())) {
                        return "已关闭";
                    }

                }
            }
            //既有待发货状态 又有待收货状态 拆单
            if (waitForSendFlag && waitForReceiveFlag) {
                return "待发货(部分发货)";
            }
            if (waitForSendFlag) {
                return "待发货";
            }
            if (waitForReceiveFlag) {
                return "待收货";
            }
        }

        return EMPTY;
    }

    /**
     * 店铺总优惠 订单总金额+运费-平台优惠-支付金额
     *
     * @param orderPayment 订单支付信息
     * @return 店铺总优惠
     */
    private String calcShopDiscount(OrderPayment orderPayment) {
        BigDecimal subtract = BigDecimal.valueOf(orderPayment.getTotalAmount()).add(BigDecimal.valueOf(orderPayment.getFreightAmount())).subtract(BigDecimal.valueOf(orderPayment.getDiscountAmount())).subtract(BigDecimal.valueOf(orderPayment.getPayAmount()));
        return subtract.divide(BigDecimal.valueOf(TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP).toString();

    }

    /**
     * 配送方式
     *
     * @param order 订单
     * @return 配送方式
     */
    private String deliveryTypeTrans(Order order) {
        JSONObject extra = order.getExtra();
        if (Objects.isNull(extra)) {
            return EMPTY;
        }
        DistributionMode distributionMode = order.getDistributionMode();
        return switch (distributionMode) {
            case INTRA_CITY_DISTRIBUTION -> "同城配送";
            case SHOP_STORE -> "到店自提";
            case EXPRESS -> "快递配送";
            case VIRTUAL -> "无需物流";
        };
    }

    /**
     * 支付方式
     *
     * @param type 支付类型
     * @return 支付方式
     */
    private String payTypeTrans(PayType type) {
        if (Objects.isNull(type)) {
            return "未支付";
        }
        return switch (type) {
            case ALIPAY -> "支付宝";
            case WECHAT -> "微信";
            case BALANCE -> "余额支付";
        };
    }

    public static class CellStyleStrategy extends AbstractMergeStrategy {

        //保存需要合并单元格的索引
        public static final ThreadLocal<Map<Integer, Integer>> MERGE_INDEX_THREAD_LOCAL = ThreadLocal.withInitial(Maps::newHashMap);


        /**
         * 初始化时，记录行号和内容
         */

        @Override
        protected void merge(Sheet sheet, Cell cell, Head head, Integer integer) {

            int FIRST_MERGE_COLUMN_INDEX = 18;
            List<Integer> mergeIndexList = Lists.newArrayList(0, 1);
            //获取行号列号
            int rowNum = cell.getRowIndex();
            int columnNum = cell.getColumnIndex();
            // 3、获取第一个Cell中的值 其中记录索引号 用于判断是否需要合并
            if (rowNum > 1 && (columnNum >= FIRST_MERGE_COLUMN_INDEX || mergeIndexList.contains(columnNum))) {
                if (MERGE_INDEX_THREAD_LOCAL.get().containsKey(rowNum)) {
                    int startRowNum = rowNum - MERGE_INDEX_THREAD_LOCAL.get().get(rowNum);
                    CellRangeAddress cellAddresses = new CellRangeAddress(startRowNum, rowNum, columnNum, columnNum);
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
