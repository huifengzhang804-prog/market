package com.medusa.gruul.overview.service.modules.export.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.overview.api.entity.DataExportRecord;
import com.medusa.gruul.overview.api.enums.DataExportStatus;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.model.DataExportRecordQueryDTO;
import com.medusa.gruul.overview.api.model.DownloadFileSseMessage;
import com.medusa.gruul.overview.service.model.constants.OverViewConstants;
import com.medusa.gruul.overview.service.model.enums.OverviewError;
import com.medusa.gruul.overview.service.modules.export.service.IDataExportRecordManageService;
import com.medusa.gruul.overview.service.mp.export.service.IDataExportRecordService;
import com.medusa.gruul.overview.service.util.FileDownLoadUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


/**
 * 下载中心数据导出记录管理
 *
 * @author jipeng
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DataExportRecordManageServiceImpl implements IDataExportRecordManageService {


    private final IDataExportRecordService dataExportRecordService;

    /**
     * 用户角色检查 判断用户是否是店铺管理员
     *
     * @return true 管理员角色  false 非管理员角色
     */
    private static Boolean shopRoleCheck() {
        AtomicReference<Boolean> shopRole = new AtomicReference<>(false);
        ISecurity.match().ifAdmin(secureUser -> shopRole.set(true))
                .ifAnySupplierAdmin(secureUser -> shopRole.set(true))
                .ifSuperAdmin(secureUser -> shopRole.set(true));
        return shopRole.get();
    }

    /**
     * 保存数据导出记录
     *
     * @param userId   用户ID
     * @param dataType 数据类型
     * @return 生成记录id
     */
    @Override
    public Long saveDataExportRecord(Long userId,
                                     Long shopId, String phone,
                                     ExportDataType dataType) {
        DataExportRecord record = new DataExportRecord();
        record.setExportUserId(userId)
                .setShopId(shopId)
                .setDataType(dataType)
                .setUserPhone(phone)
                .setStatus(DataExportStatus.PROCESSING)
                .setFileName(String.join("_", dataType.getName(), DateUtil.format(new Date(),
                        DateTimeFormatter.ofPattern("yyyyMMdd"))));
        dataExportRecordService.save(record);
        return record.getId();
    }

    /**
     * 更新导出数据记录
     *
     * @param dataExportRecord 更新导出记录DTO
     */
    @Override
    public void updateDataExportRecord(DataExportRecord dataExportRecord) {
        DataExportRecord dbRecorde = dataExportRecordService.getById(dataExportRecord.getId());
        if (Objects.isNull(dbRecorde)) {
            //数据记录不存在 可能在导出的时间段内被删除了
            log.error("数据记录不存在 可能在导出的时间段内被删除了 id= {}", dataExportRecord.getId());
            return;
        }
        //发布消息到Redis中 不管文件导出成功、失败都发布消息
        DownloadFileSseMessage message = new DownloadFileSseMessage();
        message.setFileId(dataExportRecord.getId());
        message.setSuccess(dataExportRecord.getStatus().equals(DataExportStatus.SUCCESS));
        message.setContent(dataExportRecord.getStatus().getName());
        RedisUtil.getRedisTemplate().convertAndSend(OverViewConstants.DOWN_LOAD_FILE_SSE_CHANNEL,
                JSON.toJSONString(message));
        //只能对生成中的记录进行更新
        if (!dbRecorde.getStatus().equals(DataExportStatus.PROCESSING)) {
            throw OverviewError.DATA_EXPORT_RECORD_CAN_NOT_UPDATE.exception();
        }
        SystemCode.DATA_UPDATE_FAILED.falseThrow(TenantShop.disable(()-> dataExportRecordService.updateById(dataExportRecord)));


    }

    /**
     * 批量删除
     *
     * @param ids 要删除的记录ids
     * @return 是否删除成功
     */
    @Override
    public Boolean batchRemove(List<Long> ids) {
        Boolean shopRole = shopRoleCheck();
        return dataExportRecordService.remove(ids, shopRole);
    }

    /**
     * 根据注解删除
     *
     * @param id 要删除的记录id
     */
    @Override
    public void removeById(Long id) {
        Boolean shopRole = shopRoleCheck();
        Boolean deleteFlag = dataExportRecordService.removeByIdAndRole(id, shopRole);
        SystemCode.DATA_NOT_EXIST.falseThrow(deleteFlag);
    }

    /**
     * 下载数据导出记录
     *
     * @param id       下载导出记录ID
     * @param response HTTP响应流
     */
    @Override
    public void downloadDataExportRecord(Long id,
                                         HttpServletResponse response) {
        Boolean shopRole = shopRoleCheck();
        DataExportRecord record = dataExportRecordService.downloadDataExportRecord(id, shopRole);
        //正在生成中的不能下载
        OverviewError.DATA_EXPORT_RECORD_STATUS_ERROR.trueThrow(
                DataExportStatus.PROCESSING.equals(record.getStatus()));
        //文件下载
        OverviewError.DATA_EXPORT_RECORD_DOWN_LOAD_ERROR.falseThrow(
                FileDownLoadUtil.downLoad(
                        record.getFileName(), record.getFilePath(), response));


    }

    /**
     * 数据导出记录列表查询
     *
     * @param dataExportRecordQueryDTO 查询条件
     * @return 导出记录列表
     */
    @Override
    public Page<DataExportRecordDTO> list(DataExportRecordQueryDTO dataExportRecordQueryDTO) {
        Boolean shopRole = shopRoleCheck();
        IPage<DataExportRecord> dataExportRecordPage = dataExportRecordService.queryList(
                dataExportRecordQueryDTO, shopRole);
        Page<DataExportRecordDTO> result = new Page<>();
        BeanUtil.copyProperties(dataExportRecordPage, result);
        if (!CollectionUtils.isEmpty(dataExportRecordPage.getRecords())) {
            List<DataExportRecordDTO> dataExportRecordDtos = BeanUtil.copyToList(
                    dataExportRecordPage.getRecords(), DataExportRecordDTO.class);
            for (DataExportRecordDTO dataExportRecordDTO : dataExportRecordDtos) {
                dataExportRecordDTO.setExportDataTypeStr(dataExportRecordDTO.getDataType().getName());
            }
            result.setRecords(dataExportRecordDtos);
        } else {
            result.setRecords(Lists.newArrayList());
        }

        return result;
    }

    /**
     * 获取当前用户正在导出中的记录条数
     *
     * @return 正在导出中的记录条数
     */
    @Override
    public Long generateCount() {
        LambdaQueryWrapper<DataExportRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataExportRecord::getStatus, DataExportStatus.PROCESSING)
                .eq(DataExportRecord::getExportUserId, ISecurity.userMust().getId())
                .eq(DataExportRecord::getShopId, ISecurity.userMust().getShopId());
        return dataExportRecordService.getBaseMapper().selectCount(wrapper);
    }
}
