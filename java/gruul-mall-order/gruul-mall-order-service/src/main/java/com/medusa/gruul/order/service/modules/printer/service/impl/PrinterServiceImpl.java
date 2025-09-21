package com.medusa.gruul.order.service.modules.printer.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.modules.printer.feie.api.FeieResponse;
import com.medusa.gruul.order.service.modules.printer.feie.api.IFeieApi;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.AddPrinterParam;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.BatchAddPrinterResp;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.PrinterInfoResp;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeiePrinterStatus;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeiePrinterType;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.model.PrinterConstant;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrinterEditDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrinterPageDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrinterSaveDTO;
import com.medusa.gruul.order.service.modules.printer.model.vo.PrinterRecordVO;
import com.medusa.gruul.order.service.modules.printer.mp.dao.PrintTaskDao;
import com.medusa.gruul.order.service.modules.printer.mp.dao.PrinterDao;
import com.medusa.gruul.order.service.modules.printer.mp.entity.PrintTask;
import com.medusa.gruul.order.service.modules.printer.mp.entity.Printer;
import com.medusa.gruul.order.service.modules.printer.service.PrinterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * @since 2024/8/21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrinterServiceImpl implements PrinterService {

    private final IFeieApi feieApi;
    private final PrinterDao printerDao;
    private final PrintTaskDao printTaskDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = PrinterConstant.PRINTER_BIND_LOCK_KEY)
    public void save(Long shopId, PrinterSaveDTO param) {
        //新增
        if (feieApi.config() == null) {
            throw OrderError.PRINTER_PLATFORM_NOT_CONFIG.exception();
        }
        //同一台打印机 同一个业务不能反复添加
        if (printerDao.lambdaQuery()
                .eq(Printer::getShopId, shopId)
                .eq(Printer::getMode, param.getMode())
                .eq(Printer::getBrand, param.getBrand())
                .eq(Printer::getSn, param.getSn())
                .last(SqlHelper.SQL_LIMIT_1)
                .exists()) {
            throw OrderError.PRINTER_CANNOT_ADD_SAME.exception();
        }
        //查询相同品牌的同一台打印机 纸张尺寸
        Printer sameSnPrinter = printerDao.lambdaQuery()
                .select(Printer::getSize, Printer::getKey)
                .eq(Printer::getBrand, param.getBrand())
                .eq(Printer::getSn, param.getSn())
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        //渲染打印机数据
        Printer entity = new Printer()
                .setShopId(shopId)
                .setMode(param.getMode())
                .setName(param.getName())
                .setBrand(param.getBrand())
                .setPlace(param.getPlace())
                .setSn(param.getSn())
                .setKey(param.getKey())
                .setFlowCard(param.getFlowCard());
        if (sameSnPrinter != null) {
            String key = sameSnPrinter.getKey();
            //如果key 不一致 则报错
            if (!StrUtil.nullToEmpty(key).equals(StrUtil.nullToEmpty(entity.getKey()))) {
                throw OrderError.PRINTER_ADD_FAILED.msgEx("打印机信息填写错误");
            }
            entity.setSize(sameSnPrinter.getSize());
            entity.setKey(key);
        }
        printerDao.save(entity);
        //同一太打印机不需要反复绑定
        if (sameSnPrinter != null) {
            return;
        }
        //绑定新的打印机
        FeieResponse<BatchAddPrinterResp> response = feieApi.batchAddPrinter(
                List.of(
                        new AddPrinterParam()
                                .setSn(param.getSn())
                                .setKey(param.getKey())
                                .setFlowCard(param.getFlowCard())
                )
        );
        if (!response.isSuccess()) {
            log.warn("1.打印机添加失败,{}", response);
            throw OrderError.PRINTER_ADD_FAILED.dataEx(response);
        }
        BatchAddPrinterResp data = response.getData();
        if (data.getOk().isEmpty()) {
            log.warn("2.打印机添加失败：{}", response);
            throw OrderError.PRINTER_ADD_FAILED.dataEx(data);
        }
        //直接查询打印机信息
        FeieResponse<PrinterInfoResp> printerInfoResponse = feieApi.printerInfo(param.getSn());

        //正常添加成功的打印机不会查询失败 暂是只做日志记录
        if (!printerInfoResponse.isSuccess()) {
            log.error("1.打印机信息查询失败,{}", response);
            throw OrderError.PRINTER_ADD_FAILED.dataEx(response);
        }
        PrinterInfoResp printerInfo = printerInfoResponse.getData();
        //如果不是小票机 则解绑 报错提示
        FeiePrinterType model = printerInfo.getModel();
        if (!model.isTicket()) {
            feieApi.batchDeletePrinter(Set.of(param.getSn()));
            throw OrderError.PRINTER_NOT_SUPPORT.exception();
        }
        //更新小票机尺寸信息
        printerDao.lambdaUpdate()
                .set(Printer::getSize, FeiePrinterType.P_58 == model ? FeieTicketSize.V58 : FeieTicketSize.V80)
                .eq(Printer::getShopId, shopId)
                .eq(BaseEntity::getId, entity.getId())
                .update();
    }

    @Override
    public void edit(Long shopId, PrinterEditDTO param) {
        printerDao.lambdaUpdate()
                .set(Printer::getName, param.getName())
                .set(Printer::getPlace, param.getPlace())
                .eq(Printer::getShopId, shopId)
                .eq(Printer::getMode, param.getMode())
                .eq(BaseEntity::getId, param.getId())
                .update();
    }

    @Override
    public IPage<PrinterRecordVO> page(Long shopId, PrinterPageDTO param) {
        PrinterPageDTO page = printerDao.lambdaQuery()
                .select(
                        Printer::getId, Printer::getName, Printer::getBrand,
                        Printer::getPlace, Printer::getSn, Printer::getSize,
                        Printer::getCreateTime
                )
                .eq(Printer::getShopId, shopId)
                .eq(Printer::getMode, param.getMode())
                .eq(param.getTicketSize() != null, Printer::getSize, param.getTicketSize())
                .orderByDesc(Printer::getCreateTime)
                .page(param);
        List<Printer> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            return page.convert(v -> null);
        }
        //查询是否已被打印任务绑定
        Set<Long> taskBoundPrinterIds = printTaskDao.lambdaQuery()
                .select(PrintTask::getPrinterId)
                .eq(PrintTask::getShopId, shopId)
                .eq(PrintTask::getMode, param.getMode())
                .in(PrintTask::getPrinterId, records.stream().map(BaseEntity::getId).collect(Collectors.toSet()))
                .list()
                .stream()
                .map(PrintTask::getPrinterId)
                .collect(Collectors.toSet());
        return page.convert(
                record -> {
                    FeieResponse<PrinterInfoResp> response = feieApi.printerInfo(record.getSn());
                    FeiePrinterType type = null;
                    FeiePrinterStatus status = FeiePrinterStatus.OFFLINE;
                    if (response.isSuccess()) {
                        PrinterInfoResp resp = response.getData();
                        type = resp.getModel();
                        status = resp.getStatus();
                    } else {
                        log.error("打印机信息查询失败，{}", response);
                    }
                    return new PrinterRecordVO()
                            .setId(record.getId())
                            .setName(record.getName())
                            .setBrand(record.getBrand())
                            .setSize(record.getSize())
                            .setType(type)
                            .setStatus(status)
                            .setPlace(record.getPlace())
                            .setCreateDate(record.getCreateTime().toLocalDate())
                            .setBound(taskBoundPrinterIds.contains(record.getId()));
                }
        );
    }

    @Override
    @Redisson(name = PrinterConstant.PRINTER_BIND_LOCK_KEY)
    @Redisson(name = PrinterConstant.PRINTER_DELETE_LOCK_KEY, key = "#shopId +':'+#id")
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long shopId, Long id) {
        if (printTaskDao.lambdaQuery()
                .eq(PrintTask::getShopId, shopId)
                .eq(PrintTask::getPrinterId, id)
                .exists()
        ) {
            throw OrderError.PRINTER_CANNOT_DELETE.exception();
        }
        Printer one = printerDao.lambdaQuery()
                .select(Printer::getSn, Printer::getBrand)
                .eq(Printer::getShopId, shopId)
                .eq(BaseEntity::getId, id)
                .one();
        if (one == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        printerDao.lambdaUpdate()
                .eq(Printer::getShopId, shopId)
                .eq(BaseEntity::getId, id)
                .remove();
        //存在同一台打印机 就不需要解绑
        if (printerDao.lambdaQuery()
                .select(Printer::getSize)
                .eq(Printer::getBrand, one.getBrand())
                .eq(Printer::getSn, one.getSn())
                .exists()) {
            return;
        }

        //解绑打印机
        FeieResponse<BatchAddPrinterResp> response = feieApi.batchDeletePrinter(Set.of(one.getSn()));
        if (!response.isSuccess()) {
            log.error("打印机解绑失败，{}", response);
            throw OrderError.PRINTER_UNBIND_ERROR.dataEx(response);
        }
        BatchAddPrinterResp data = response.getData();
        if (data.getOk().isEmpty()) {
            throw OrderError.PRINTER_UNBIND_ERROR.dataEx(data);
        }


    }

}
