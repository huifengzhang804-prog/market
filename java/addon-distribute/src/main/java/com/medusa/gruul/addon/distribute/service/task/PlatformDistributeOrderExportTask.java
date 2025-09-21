package com.medusa.gruul.addon.distribute.service.task;

import cn.hutool.core.date.DatePattern;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Lists;
import com.medusa.gruul.addon.distribute.model.dto.DistributorOrderQueryDTO;
import com.medusa.gruul.addon.distribute.model.dto.PlatformDistributeOrderExportDTO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorMainOrderVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorOrderPageVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorOrderVO;
import com.medusa.gruul.addon.distribute.service.DistributeOrderHandleService;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.order.api.entity.OrderPayment;
import com.medusa.gruul.order.api.model.OrderInfoDTO;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.user.api.model.UserDataVO;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author jipeng
 * @description: 分销订单导出任务
 * @since 2025/2/15
 */
@Slf4j

public class PlatformDistributeOrderExportTask extends AbstractDistributeOrderExportTask {

    public static final String PLATFORM_DISTRIBUTE_ORDER_FILE_PATH = "/temp/平台端--分销订单.xlsx";


    public PlatformDistributeOrderExportTask(
            DistributeOrderHandleService distributeOrderHandleService,
            PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService,
            DataExportRecordRpcService dataExportRecordRpcService,
            UserRpcService userRpcService,
            OrderRpcService orderRpcService,
            DistributorOrderQueryDTO queryDTO,
            DataExportRecordDTO dataExportRecordDTO) {
        super(distributeOrderHandleService, pigeonChatStatisticsRpcService,
                dataExportRecordRpcService, userRpcService, orderRpcService,
                queryDTO, dataExportRecordDTO);
    }

    @Override
    public void run() {

        File tempExcelFile = generateTempExcelFile(PLATFORM_DISTRIBUTE_ORDER_FILE_PATH);
        taskRun(tempExcelFile);

    }


    @Override
    public Integer generateExcel(File excelFile) {
        ExcelWriter excelWriter = null;
        AtomicInteger counter = new AtomicInteger(CommonPool.NUMBER_ONE);

        try {
            excelWriter = fetchExcelWriter(excelFile);
            WriteSheet writeSheet = EasyExcel.writerSheet(SHEET_NAME).build();
            while (true) {
                DistributorOrderPageVO distributorOrderPageVO = getDistributeOrderHandleService().orderPage(
                        getQueryDTO());
                List<DistributorMainOrderVO> records = distributorOrderPageVO.getPage().getRecords();
                if (records.isEmpty()) {
                    break;
                }

                getQueryDTO().setCurrent(getQueryDTO().getCurrent() + CommonPool.NUMBER_ONE);
                excelWriter.write(data(records, counter), writeSheet);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(excelWriter)) {
                excelWriter.close();
            }
        }

        return counter.get() - 1;
    }

    /**
     * 封装数据
     *
     * @param records
     * @param index
     * @return
     */
    public List<PlatformDistributeOrderExportDTO> data(List<DistributorMainOrderVO> records, AtomicInteger index) {
        List<PlatformDistributeOrderExportDTO> result = Lists.newArrayList();
        Set<String> orderNos = records.stream().map(DistributorMainOrderVO::getOrderNo).collect(Collectors.toSet());
        Set<Long> userIds = records.stream().map(DistributorMainOrderVO::getBuyerId).collect(Collectors.toSet());
        Map<Long, UserDataVO> userDataVOMap = getUserRpcService().queryUserBaseInfo(userIds);
        List<OrderInfoDTO> orderInfoDTOS = getOrderRpcService().queryOrderInfoForExport(orderNos);
        Map<String, OrderInfoDTO> orderInfoDTOMap = orderInfoDTOS.stream()
                .collect(Collectors.toMap(OrderInfoDTO::getOrderNo, item -> item));
        //循环订单
        for (DistributorMainOrderVO order : records) {
            order.getItems().forEach(item -> {
                dealItem(order, item, result, index, userDataVOMap, orderInfoDTOMap);
            });
        }
        return result;
    }

    /**
     * 处理每一行数据
     *
     * @param order
     * @param item
     * @param result
     * @param index
     * @param userDataVOMap
     * @param orderInfoDTOMap
     */
    private void dealItem(DistributorMainOrderVO order,
                          DistributorOrderVO item,
                          List<PlatformDistributeOrderExportDTO> result,
                          AtomicInteger index, Map<Long, UserDataVO> userDataVOMap, Map<String, OrderInfoDTO> orderInfoDTOMap) {
        PlatformDistributeOrderExportDTO row = new PlatformDistributeOrderExportDTO();
        //编号
        row.setIndex(String.valueOf(index.getAndIncrement()));
        //订单编号
        row.setNo(order.getOrderNo());
        OrderInfoDTO orderInfoDTO = orderInfoDTOMap.get(order.getOrderNo());
        if (Objects.nonNull(orderInfoDTO)) {
            //订单状态
            row.setStatus(orderInfoDTO.getOrderStatusContent());
            OrderPayment payment = orderInfoDTO.getPayment();
            if (Objects.isNull(payment)) {
                row.setPayTime(StringUtils.EMPTY);
                row.setPayStatus("未付款");
            } else {
                row.setPayStatus("已付款");
                if (Objects.nonNull(payment.getPayTime())) {
                    row.setPayTime(payment.getPayTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
                } else {
                    row.setPayTime("-");
                }
            }
        } else {
            row.setStatus("查询不到订单");
            row.setPayStatus("-");
            row.setPayTime("-");

        }
        if (Objects.isNull(userDataVOMap.get(order.getBuyerId()))) {
            row.setBuyerPhone(StringUtils.EMPTY);
            row.setBuyerNickName(StringUtils.EMPTY);
        } else {
            row.setBuyerPhone(userDataVOMap.get(order.getBuyerId()).getMobile());
            row.setBuyerNickName(userDataVOMap.get(order.getBuyerId()).getNickname());
        }

        row.setSpuId(String.valueOf(item.getProductId()));
        row.setSpuName(item.getProductName());
        row.setSpec(item.getSpecs().stream().collect(Collectors.joining(",")));
        row.setSkuId(String.valueOf(item.getSkuId()));
        row.setSalePrice(dealPrice(item.getDealPrice()));
        row.setBuyNum(String.valueOf(item.getNum()));
        row.setPayable(dealPrice(order.getPayAmount()));
        row.setShopName(order.getShopName());
        row.setIsInner(order.getPurchase() ? "是" : "否");
        if (order.getPurchase()) {
            row.setInnerCommission(dealPrice(item.getOne().getBonus()));
            row.setFirstCommission(StringUtils.EMPTY);
            row.setSecondCommission(StringUtils.EMPTY);
            row.setThirdCommission(StringUtils.EMPTY);
        } else {
            row.setInnerCommission(dealPrice(item.getOne().getBonus() +
                    item.getThree().getBonus() + item.getThree().getBonus()));
            row.setFirstCommission(dealPrice(item.getOne().getBonus()));
            row.setSecondCommission(dealPrice(item.getTwo().getBonus()));
            row.setThirdCommission(dealPrice(item.getThree().getBonus()));

        }
        //结算状态
        row.setSettlementStatus(dealItemStatus(item.getOrderStatus()));

        //下单时间
        row.setOrderTime(order.getCreateTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        if (Objects.nonNull(item.getOne().getUserId())) {
            row.setFirstDistributeNickName(item.getOne().getName());
            row.setFirstDistributePhone(item.getOne().getMobile());
        } else {
            row.setFirstDistributeNickName(StringUtils.EMPTY);
            row.setFirstDistributePhone(StringUtils.EMPTY);
        }
        if (Objects.nonNull(item.getTwo().getUserId())) {
            row.setSecondDistributeNickName(item.getTwo().getName());
            row.setSecondDistributePhone(item.getTwo().getMobile());
        } else {
            row.setSecondDistributeNickName(StringUtils.EMPTY);
            row.setSecondDistributePhone(StringUtils.EMPTY);
        }
        if (Objects.nonNull(item.getThree().getUserId())) {
            row.setThirdDistributeNickName(item.getThree().getName());
            row.setThirdDistributePhone(item.getThree().getMobile());
        } else {
            row.setThirdDistributeNickName(StringUtils.EMPTY);
            row.setThirdDistributePhone(StringUtils.EMPTY);
        }
        result.add(row);


    }


    ExcelWriter fetchExcelWriter(File excelFile) throws IOException {
        return EasyExcel.write(excelFile, PlatformDistributeOrderExportDTO.class)
//                    .registerWriteHandler(new CellStyleStrategy())
                .withTemplate(new ClassPathResource(PLATFORM_DISTRIBUTE_ORDER_FILE_PATH).getInputStream())
                .needHead(Boolean.FALSE)
                .build();

    }
}
