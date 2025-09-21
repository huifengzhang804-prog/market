package com.medusa.gruul.order.service.modules.printer.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.api.enums.DiscountSourceType;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.PrintLink;
import com.medusa.gruul.order.api.enums.ShopOrderStatus;
import com.medusa.gruul.order.api.model.OrderDestination;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonDistributionSupporter;
import com.medusa.gruul.order.service.modules.printer.addon.OrderPlatformSupporter;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.model.PrinterConstant;
import com.medusa.gruul.order.service.modules.printer.model.bo.PrintPublishBO;
import com.medusa.gruul.order.service.modules.printer.model.dto.OrderPrintDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTaskDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTaskPageDTO;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintPlaceHolder;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintTemplateConfig;
import com.medusa.gruul.order.service.modules.printer.model.template.conf.OrderStatistic;
import com.medusa.gruul.order.service.modules.printer.model.template.conf.PrintOrderStatistic;
import com.medusa.gruul.order.service.modules.printer.model.template.conf.PrintProduct;
import com.medusa.gruul.order.service.modules.printer.model.template.conf.Products;
import com.medusa.gruul.order.service.modules.printer.model.vo.PrintTaskRecordVO;
import com.medusa.gruul.order.service.modules.printer.mp.dao.PrintTaskDao;
import com.medusa.gruul.order.service.modules.printer.mp.dao.PrintTemplateDao;
import com.medusa.gruul.order.service.modules.printer.mp.dao.PrinterDao;
import com.medusa.gruul.order.service.modules.printer.mp.entity.PrintTask;
import com.medusa.gruul.order.service.modules.printer.mp.entity.PrintTemplate;
import com.medusa.gruul.order.service.modules.printer.mp.entity.Printer;
import com.medusa.gruul.order.service.modules.printer.mq.service.PrintRabbitService;
import com.medusa.gruul.order.service.modules.printer.service.PrintTaskService;
import com.medusa.gruul.order.service.mp.service.*;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * @since 2024/8/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrintTaskServiceImpl implements PrintTaskService {

    private final SqlSessionFactory sqlSessionFactory;
    private final PrinterDao printerDao;
    private final PrintTaskDao printTaskDao;
    private final PrintTemplateDao printTemplateDao;
    private final OrderPlatformSupporter orderPlatformSupporter;

    private final PrintRabbitService printService;
    private final OrderRabbitService orderRabbitService;
    private final IOrderService orderService;
    private final IOrderPaymentService orderPaymentService;
    private final IShopOrderService shopOrderService;
    private final IOrderReceiverService orderReceiverService;
    private final IShopOrderItemService shopOrderItemService;
    private final IOrderDiscountService orderDiscountService;
    private final IOrderDiscountItemService orderDiscountItemService;
    private final OrderAddonDistributionSupporter addonDistributionSupporter;


    @Override
    @Redisson(name = PrinterConstant.PRINT_TASK_LINK_LOCK_KEY, key = "#shopId")
    @Redisson(name = PrinterConstant.PRINTER_DELETE_LOCK_KEY, key = "#shopId +':'+#param.printerId")
    public void saveOrUpdate(Long shopId, PrintTaskDTO param) {
        Long printerId = param.getPrinterId();
        PrintMode mode = param.getMode();
        Printer printer = printerDao.lambdaQuery()
                .select(Printer::getSize, Printer::getSn)
                .eq(Printer::getShopId, shopId)
                .eq(BaseEntity::getId, printerId)
                .eq(Printer::getMode, mode)
                .one();
        if (printer == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        //任务id 为空是新增 不为空是编辑
        Long id = param.getId();
        //模板id
        Long templateId = param.getTemplateId();

        //编辑且模板未修改 使用之前的模板快照 否则查询新的模板
        if (id != null && templateId == -1) {
            printTaskDao.lambdaUpdate()
                    .set(PrintTask::getName, param.getName())
                    .set(PrintTask::getPrinterId, printerId)
                    .set(PrintTask::getScene, param.getScene())
                    .set(PrintTask::getTimes, param.getTimes())
                    .eq(BaseEntity::getId, id)
                    .eq(PrintTask::getShopId, shopId)
                    .eq(PrintTask::getMode, mode)
                    .update();
            return;
        }
        PrintTemplate template = printTemplateDao.lambdaQuery()
                .select(
                        PrintTemplate::getName,
                        PrintTemplate::getSize,
                        PrintTemplate::getLink,
                        PrintTemplate::getConfig,
                        PrintTemplate::getTemplate
                )
                .eq(PrintTemplate::getShopId, shopId)
                .eq(BaseEntity::getId, templateId)
                .eq(PrintTemplate::getMode, mode)
                .one();
        if (template == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }

        //打印机尺寸和模板尺寸需要完全匹配
        if (printer.getSize() != template.getSize()) {
            throw OrderError.PRINTER_SIZE_NOT_MATCHED.exception();
        }
        //同一打印类型只能添加一次
        PrintLink link = template.getLink();
        if (printTaskDao.lambdaQuery()
                .eq(PrintTask::getShopId, shopId)
                .eq(PrintTask::getMode, mode)
                .eq(PrintTask::getLink, link)
                .ne(id != null, BaseEntity::getId, id)
                .exists()) {
            throw OrderError.PRINTER_TASK_EXIST_SAME_LINK.exception();
        }

        PrintTask printTask = (PrintTask) new PrintTask()
                .setShopId(shopId)
                .setMode(mode)
                .setName(param.getName())
                .setPrinterId(printerId)
                .setPrinterSn(printer.getSn())
                .setScene(param.getScene())
                .setTimes(param.getTimes())
                //模板快照
                .setLink(link)
                .setTemplateName(template.getName())
                .setSize(template.getSize())
                .setConfig(template.getConfig())
                .setTemplate(template.getTemplate())
                .setId(id);
        printTaskDao.saveOrUpdate(printTask);
    }


    @Override
    public IPage<PrintTaskRecordVO> page(Long shopId, PrintTaskPageDTO param) {
        PrintTaskPageDTO page = printTaskDao.lambdaQuery()
                .select(
                        BaseEntity::getId, PrintTask::getName, PrintTask::getScene,
                        PrintTask::getTimes, PrintTask::getPrinterId, PrintTask::getLink,
                        PrintTask::getTemplateName, PrintTask::getSize, BaseEntity::getCreateTime
                )
                .eq(PrintTask::getShopId, shopId)
                .eq(param.getMode() != null, PrintTask::getMode, param.getMode())
                .orderByDesc(BaseEntity::getCreateTime)
                .page(param);
        List<PrintTask> records = page.getRecords();
        if (records.isEmpty()) {
            return page.convert(v -> null);
        }
        Map<Long, String> printerNameMap = printerDao.lambdaQuery()
                .select(BaseEntity::getId, Printer::getName)
                .eq(Printer::getShopId, shopId)
                .eq(Printer::getMode, param.getMode())
                .in(BaseEntity::getId, records.stream().map(PrintTask::getPrinterId).collect(Collectors.toSet()))
                .list()
                .stream()
                .collect(
                        Collectors.toMap(
                                BaseEntity::getId,
                                Printer::getName
                        )
                );
        return page.convert(
                record -> new PrintTaskRecordVO()
                        .setId(record.getId())
                        .setName(record.getName())
                        .setPrinterId(record.getPrinterId())
                        .setPrinterName(printerNameMap.getOrDefault(record.getPrinterId(), StrUtil.EMPTY))
                        .setLink(record.getLink())
                        .setTemplateName(record.getTemplateName())
                        .setScene(record.getScene())
                        .setTimes(record.getTimes())
                        .setSize(record.getSize())
                        .setCreateDate(record.getCreateTime().toLocalDate())

        );
    }

    @Override
    public void delete(Long shopId, Long id) {
        printTaskDao.lambdaUpdate()
                .eq(PrintTask::getShopId, shopId)
                .eq(BaseEntity::getId, id)
                .remove();
    }

    @Override
    public Map<PrintMode, Set<PrintLink>> printLinks(Long shopId) {
        return printTaskDao.lambdaQuery()
                .select(PrintTask::getLink, PrintTask::getMode)
                .eq(PrintTask::getShopId, shopId)
                .list()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                PrintTask::getMode,
                                Collectors.mapping(
                                        PrintTask::getLink,
                                        Collectors.toSet()
                                )
                        )
                );
    }

    @Override
    public void printOrder(OrderPrintDTO param) {
        String orderNo = param.getOrderNo();
        PrintMode mode = param.getMode();
        Set<Long> shopIds = param.getShopIds();
        //店铺信息
        Map<Long, ShopOrder> shopOrderMap = TenantShop.disable(
                        () -> shopOrderService.lambdaQuery()
                                .select(
                                        ShopOrder::getShopId, ShopOrder::getShopName,
                                        ShopOrder::getRemark, BaseEntity::getCreateTime, ShopOrder::getExtra,
                                        ShopOrder::getTotalAmount, ShopOrder::getFreightAmount, ShopOrder::getDiscountAmount
                                )
                                .eq(ShopOrder::getStatus, ShopOrderStatus.OK)
                                .eq(ShopOrder::getOrderNo, orderNo)
                                .in(CollUtil.isNotEmpty(shopIds), ShopOrder::getShopId, shopIds)
                                .list()
                )
                .stream()
                .collect(Collectors.toMap(ShopOrder::getShopId, Function.identity()));
        if (CollUtil.isEmpty(shopOrderMap)) {
            return;
        }
        Set<Long> finalShopIds = shopOrderMap.keySet();
        //取件码
        Map<Long, Long> pickupCodeMap = pickupCodes(shopOrderMap.values());

        //付款后自动打印的任务
        List<PrintTask> tasks = printTaskDao.lambdaQuery()
                .select(PrintTask::getShopId, PrintTask::getTimes, PrintTask::getPrinterSn, PrintTask::getSize, PrintTask::getConfig, PrintTask::getTemplate)
                .in(PrintTask::getShopId, finalShopIds)
                .eq(param.getScene() != null, PrintTask::getScene, param.getScene())
                .eq(param.getLink() != null, PrintTask::getLink, param.getLink())
                .eq(PrintTask::getMode, mode)
                .list();
        if (CollUtil.isEmpty(tasks)) {
            return;
        }
        // 查询支付信息
        OrderPayment payment = orderPaymentService.lambdaQuery()
                .select(OrderPayment::getPayTime, OrderPayment::getType)
                .eq(OrderPayment::getOrderNo, orderNo)
                .one();
        //订单目的地信息（收货人、门店信息）
        Map<Long, OrderDestination> receiverMap = receivers(orderNo, mode, finalShopIds);
        //店铺订单商品信息
        Map<Long, List<ShopOrderItem>> shopOrderItemMap = TenantShop.disable(() -> shopOrderItemService.lambdaQuery()
                        .select(
                                ShopOrderItem::getShopId, BaseEntity::getId,
                                ShopOrderItem::getProductName, ShopOrderItem::getNum,
                                ShopOrderItem::getSpecs, ShopOrderItem::getSalePrice
                        )
                        .eq(ShopOrderItem::getOrderNo, orderNo)
                        .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                        .orderByAsc(ShopOrderItem::getShopId, ShopOrderItem::getProductId)
                        .list()
                )
                .stream()
                .collect(
                        Collectors.groupingBy(ShopOrderItem::getShopId)
                );
        //折扣信息
        Map<Long, List<OrderDiscountItem>> shopDiscountMap = shopDiscounts(orderNo, finalShopIds);
        //渲染打印数据
        Map<Long, List<PrintTask>> shopTaskMap = tasks.stream()
                .collect(Collectors.groupingBy(PrintTask::getShopId, Collectors.toList()));
        shopTaskMap.forEach(
                (shopId, shopTasks) -> {
                    //目的地
                    OrderDestination destination = receiverMap.get(shopId);
                    destination = destination == null ? receiverMap.get(null) : destination;
                    ShopOrder shopOrder = shopOrderMap.get(shopId);
                    //订单备注
                    //商品信息
                    List<ShopOrderItem> shopOrderItems = shopOrderItemMap.get(shopId);
                    List<PrintProduct> printProducts = shopOrderItems
                            .stream()
                            .map(item -> new PrintProduct()
                                    .setName(item.getProductName())
                                    .setSpecs(item.getSpecs())
                                    .setSkuId(item.getId())
                                    .setNum(item.getNum())
                                    .setPrice(item.getSalePrice())
                            )
                            .toList();
                    PrintOrderStatistic printOrderStatistic = new PrintOrderStatistic()
                            .setProductNum(shopOrderItems.stream().map(ShopOrderItem::getNum).reduce(0, Integer::sum))
                            .setTotalPrice(shopOrder.getTotalAmount())
                            .setFreight(shopOrder.getFreightAmount())
                            .setPlatformDiscount(0L)
                            .setShopDiscount(0L)
                            .setTotalDiscount(shopOrder.getDiscountAmount())
                            .setPayPrice(shopOrder.payAmount());
                    //平台、店铺折扣计算
                    List<OrderDiscountItem> discountItems = shopDiscountMap.get(shopId);
                    if (CollUtil.isNotEmpty(discountItems)) {
                        Set<Long> itemIds = shopOrderItems.stream().map(BaseEntity::getId).collect(Collectors.toSet());
                        for (OrderDiscountItem discountItem : discountItems) {
                            if (!itemIds.contains(discountItem.getItemId())) {
                                continue;
                            }
                            boolean platform = discountItem.getSourceType().isPlatform();
                            if (platform) {
                                printOrderStatistic.setPlatformDiscount(
                                        printOrderStatistic.getPlatformDiscount() + discountItem.getDiscountAmount()
                                );
                            } else {
                                printOrderStatistic.setShopDiscount(
                                        printOrderStatistic.getShopDiscount() + discountItem.getDiscountAmount()
                                );
                            }
                        }
                    }
                    //打印数据
                    Map<Object, String> params = new HashMap<>();
                    params.put(PrintPlaceHolder.pickupCode, pickupCodeMap.get(shopId).toString());
                    //todo 获取平台名称
                    params.put(PrintPlaceHolder.platformName, getPlatformName());
                    params.put(PrintPlaceHolder.shopName, shopOrder.getShopName());
                    params.put(PrintPlaceHolder.orderRemark, shopOrder.remarkOnly());
                    params.put(PrintPlaceHolder.orderNo, orderNo);
                    params.put(PrintPlaceHolder.payType, payment.getType().getDesc());
                    params.put(PrintPlaceHolder.orderTime, shopOrder.getCreateTime().format(FastJson2.DATETIME_FORMATTER));
                    params.put(PrintPlaceHolder.payTime, payment.getPayTime().format(FastJson2.DATETIME_FORMATTER));
                    params.put(PrintPlaceHolder.targetName, destination.getName());
                    params.put(PrintPlaceHolder.targetMobile, PrintMode.STORE_PICKUP_SELF == mode ? destination.getMobile() : DesensitizedUtil.mobilePhone(destination.getMobile()));
                    params.put(PrintPlaceHolder.targetAddress, destination.getAddress());

                    //遍历打印任务渲染打印数据打印小票
                    for (PrintTask task : shopTasks) {
                        //渲染打印数据
                        String fxml = task.getTemplate();
                        FeieTicketSize ticketSize = task.getSize();
                        PrintTemplateConfig config = task.getConfig();
                        OrderStatistic orderStatistic = config.getOrderStatistic();
                        Products products = config.getProducts();
                        if (products != null) {
                            params.put(PrintPlaceHolder.productListInf, products.fxml(ticketSize, printProducts));
                        } else {
                            params.remove(PrintPlaceHolder.productListInf);
                        }
                        //订单统计信息
                        if (orderStatistic != null) {
                            params.put(PrintPlaceHolder.orderStatistic, orderStatistic.fxml(ticketSize, printOrderStatistic));
                        } else {
                            params.remove(PrintPlaceHolder.orderStatistic);
                        }
                        //渲染打印数据
                        fxml = StrUtil.format(fxml, params);
                        printService.print(
                                new PrintPublishBO()
                                        .setSn(task.getPrinterSn())
                                        .setContent(fxml)
                                        .setTimes(task.getTimes())
                        );
                    }
                }
        );
    }

    private String getPlatformName() {
        String platformName = null;
        try {
            platformName = orderPlatformSupporter.platformName();
        } catch (Exception e) {
            log.error("获取平台名称失败", e);
        }
        return StrUtil.emptyIfNull(platformName);
    }

    @Override
    public void printOrder(Long shopId, String orderNo, PrintLink link) {
        Order order = orderService.lambdaQuery()
                .select(Order::getDistributionMode)
                .eq(Order::getNo, orderNo)
                .one();
        if (order == null) {
            throw OrderError.ORDER_NOT_EXIST.exception();
        }
        DistributionMode distributionMode = order.getDistributionMode();
        PrintMode mode = switch (distributionMode) {
            case INTRA_CITY_DISTRIBUTION -> PrintMode.INTRA_CITY;
            case SHOP_STORE -> PrintMode.STORE_PICKUP_SELF;
            default -> throw OrderError.ORDER_NOT_EXIST.exception();
        };
        //发送打印任务消息
        orderRabbitService.sendPrintOrder(
                new OrderPrintDTO()
                        .setOrderNo(orderNo)
                        .setMode(mode)
                        .setLink(link)
                        .setShopIds(Set.of(shopId))
        );
    }

    /**
     * 获取取餐码或生成新的取餐码
     *
     * @return key 店铺 id value 取餐码
     */
    private Map<Long, Long> pickupCodes(Collection<ShopOrder> shopOrders) {
        Map<Long, Long> pickupCodeMap = new HashMap<>(shopOrders.size());
        for (ShopOrder shopOrder : shopOrders) {
            pickupCodeMap.put(
                    shopOrder.getShopId(),
                    shopOrder.getExtra().getPickupCode()
            );
        }
        return pickupCodeMap;
    }

    /**
     * 渲染目的地信息
     *
     * @param orderNo 订单号
     * @param mode    打印单关联的业务模式
     * @param shopIds 店铺 id 集合 当模式是自提时只能有一个店铺 id
     * @return key店铺 id ，value  目的地信息
     */
    private Map<Long, OrderDestination> receivers(String orderNo, PrintMode mode, Set<Long> shopIds) {
        Map<Long, OrderDestination> receiverMap = new HashMap<>();
        //门店自提 查询门店信息 门店名称 门店负责人电话 门店地址
        if (PrintMode.STORE_PICKUP_SELF == mode) {
            Order one = orderService.query()
                    //买家 id作为门店 id
                    .select("extra ->> '$.shopStoreId' AS buyer_id")
                    .eq("`no`", orderNo)
                    .one();
            //todo 查询门店信息
            Long shopId = shopIds.iterator().next();
            receiverMap.put(
                    shopId,
                    addonDistributionSupporter.storeAddress(shopId, one.getBuyerId())
            );
        } else {
            //否则查询订单的收货地址
            orderReceiverService.lambdaQuery()
                    .select(
                            OrderReceiver::getShopId, OrderReceiver::getName, OrderReceiver::getMobile,
                            OrderReceiver::getArea, OrderReceiver::getAddress
                    )
                    .eq(OrderReceiver::getOrderNo, orderNo)
                    .and(
                            qw -> qw.isNull(OrderReceiver::getShopId)
                                    .or()
                                    .in(OrderReceiver::getShopId, shopIds)
                    ).list()
                    .forEach(receiver -> receiverMap.put(
                                    receiver.getShopId(),
                                    new OrderDestination()
                                            .setName(receiver.getName())
                                            .setMobile(receiver.getMobile())
                                            .setAddress(receiver.fullAddress())
                            )
                    );
        }
        return receiverMap;
    }

    /**
     * 查询店铺折扣信息
     *
     * @param orderNo 订单号
     * @param shopIds 店铺 id 集合
     * @return key 店铺 id , value 折扣项
     */
    public Map<Long, List<OrderDiscountItem>> shopDiscounts(String orderNo, Set<Long> shopIds) {
        Map<Long, DiscountSourceType> discountSourceTypeMap = orderDiscountService.lambdaQuery()
                .select(BaseEntity::getId, OrderDiscount::getSourceType)
                .eq(OrderDiscount::getOrderNo, orderNo)
                .list()
                .stream()
                .collect(
                        Collectors.toMap(BaseEntity::getId, OrderDiscount::getSourceType)
                );
        return MapUtil.isEmpty(discountSourceTypeMap)
                ? Map.of()
                : orderDiscountItemService.lambdaQuery()
                .select(OrderDiscountItem::getShopId, OrderDiscountItem::getItemId, OrderDiscountItem::getDiscountId, OrderDiscountItem::getDiscountAmount)
                .in(OrderDiscountItem::getShopId, shopIds)
                .in(OrderDiscountItem::getDiscountId, discountSourceTypeMap.keySet())
                .list()
                .stream()
                .peek(item -> item.setSourceType(discountSourceTypeMap.get(item.getDiscountId())))
                .collect(Collectors.groupingBy(OrderDiscountItem::getShopId));
    }


}
