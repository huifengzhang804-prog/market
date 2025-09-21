package com.medusa.gruul.overview.service.mp.export.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.overview.api.entity.DataExportRecord;
import com.medusa.gruul.overview.api.model.DataExportRecordQueryDTO;
import com.medusa.gruul.overview.service.mp.export.mapper.DataExportRecordMapper;
import com.medusa.gruul.overview.service.mp.export.service.IDataExportRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * 数据导出记录mapper层service类
 * @author jipeng
 */
@Service
@Slf4j
public class DataExportRecordServiceImpl extends
        ServiceImpl<DataExportRecordMapper, DataExportRecord> implements IDataExportRecordService {

    /**
     * 删除下载记录
     * @param id 记录id
     * @param shopRole 是否是店铺管理员角色
     * @return 是否删除成功 成功true 失败false
     */
    @Override
    public Boolean removeByIdAndRole(Long id, Boolean shopRole) {

        DataExportRecord dataExportRecord = getBaseMapper().selectOne(
                new LambdaQueryWrapper<DataExportRecord>().eq(DataExportRecord::getId, id)
                        .eq(DataExportRecord::getDeleted, CommonPool.NUMBER_ZERO)
                        .eq(DataExportRecord::getShopId, ISecurity.userMust().getShopId())
                        //非店铺管理员级别的角色 只能删除自己的下载记录
                        .eq(!shopRole, DataExportRecord::getExportUserId, ISecurity.userMust().getId()
                        ));

        if (Objects.isNull(dataExportRecord)) {
            return Boolean.FALSE;
        }
        SystemCode.DATA_DELETE_FAILED.falseThrow(removeById(id));
        return Boolean.TRUE;
    }

  /**
   * 批量删除
   * @param ids 记录ids
   * @param shopRole 角色
   * @return 是否删除成功 成功true 失败false
   */
    @Override
    public Boolean remove(List<Long> ids, Boolean shopRole) {
        List<DataExportRecord> dbRecorders = getBaseMapper().selectList(
                new LambdaQueryWrapper<DataExportRecord>().in(DataExportRecord::getId, ids)
                        .eq(DataExportRecord::getDeleted, CommonPool.NUMBER_ZERO)
                        .eq(DataExportRecord::getShopId, ISecurity.userMust().getShopId())
                        //非店铺级别的角色 需要只能删除自己的下载记录
                        .eq(!shopRole, DataExportRecord::getExportUserId, ISecurity.userMust().getId()));

        if (CollectionUtils.isEmpty(dbRecorders)) {
            return Boolean.FALSE;
        }
        List<Long> dbIds = dbRecorders.stream().map(DataExportRecord::getId)
                .collect(Collectors.toList());

        SystemCode.DATA_DELETE_FAILED.falseThrow(SpringUtil.getBean(DataExportRecordServiceImpl.class).removeByIds(dbIds));
        return Boolean.TRUE;
    }

  /**
   * 下载数据导出记录
   * @param id 记录id
   * @param shopRole 角色
   * @return
   */
    @Override
    public DataExportRecord downloadDataExportRecord(Long id, Boolean shopRole) {
        Optional<DataExportRecord> dataExportRecord = lambdaQuery().eq(DataExportRecord::getId, id)
                .eq(DataExportRecord::getDeleted, CommonPool.NUMBER_ZERO)
                .eq(DataExportRecord::getShopId, ISecurity.userMust().getShopId())
                .eq(!shopRole, DataExportRecord::getExportUserId, ISecurity.userMust().getId()).oneOpt();

        SystemCode.DATA_NOT_EXIST.falseThrow(dataExportRecord.isPresent());
        return dataExportRecord.get();
    }

  /**
   * 查询数据导出记录
   * @param queryDTO 查询条件
   * @param shopRole 角色
   * @return
   */
    @Override
    public IPage<DataExportRecord> queryList(DataExportRecordQueryDTO queryDTO,
                                             Boolean shopRole) {
        LambdaQueryWrapper<DataExportRecord> cond = new LambdaQueryWrapper<DataExportRecord>()
                .eq(DataExportRecord::getDeleted, CommonPool.NUMBER_ZERO)
                //非店铺级别的角色 需要只能查询自己的下载记录
                .eq(!shopRole, DataExportRecord::getExportUserId, ISecurity.userMust().getId())
                .eq(DataExportRecord::getShopId, ISecurity.userMust().getShopId())
                //数据类型
                .eq(Objects.nonNull(queryDTO.getDataType()), DataExportRecord::getDataType,
                        queryDTO.getDataType())
                .eq(StringUtils.hasLength(queryDTO.getUserPhone()), DataExportRecord::getUserPhone, queryDTO.getUserPhone())
                .eq(Objects.nonNull(queryDTO.getStatus()), DataExportRecord::getStatus,
                        queryDTO.getStatus())
                .ge(Objects.nonNull(queryDTO.getExportStartDate()), DataExportRecord::getCreateTime,
                        queryDTO.getExportStartDate())
                .le(Objects.nonNull(queryDTO.getExportEndDate()), DataExportRecord::getCreateTime,
                        queryDTO.getExportEndDate());
        Page<DataExportRecord> objectPage = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        objectPage.setOrders(Lists.newArrayList(OrderItem.desc("create_time")));

        return getBaseMapper().selectPage(objectPage, cond);
    }
}
