package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.user.service.model.dto.MemberPurchaseQueryDTO;
import com.medusa.gruul.user.service.model.vo.MemberPurchaseHistoryVo;
import com.medusa.gruul.user.service.mp.entity.MemberPurchaseHistory;
import com.medusa.gruul.user.service.mp.service.IMemberPurchaseHistoryService;
import com.medusa.gruul.user.service.service.IMemberPurchaseService;
import com.medusa.gruul.user.service.task.MemberPurchaseHistoryExportTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executor;

/**

 * @author jipeng
 * @Description 会员流式Service
 * @date 2022-11-15 15:12
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberPurchaseServiceImpl implements IMemberPurchaseService {


    private final Executor globalExecutor;
    private final IMemberPurchaseHistoryService memberPurchaseHistoryService;
    private final DataExportRecordRpcService dataExportRecordRpcService;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;

    /**
     * 会员记录列表查询
     *
     * @param queryDTO 会员记录列表
     * @return 会员记录
     */
    @Override
    public Page<MemberPurchaseHistoryVo> list(MemberPurchaseQueryDTO queryDTO) {
        Page<MemberPurchaseHistory> page = memberPurchaseHistoryService.queryList(queryDTO);
        List<MemberPurchaseHistory> records = page.getRecords();
        Page<MemberPurchaseHistoryVo> result = new Page<>();
        BeanUtil.copyProperties(page, result);
        if (CollectionUtil.isEmpty(records)) {
            return result;
        }

        List<MemberPurchaseHistoryVo> memberPurchaseHistoryVos = BeanUtil.copyToList(records,
                MemberPurchaseHistoryVo.class);
        if (CollectionUtil.isNotEmpty(memberPurchaseHistoryVos)) {
            memberPurchaseHistoryVos = memberPurchaseHistoryVos.stream().peek(x -> {
                x.setEffectiveStr(x.getEffectiveDurationType().getMsg());
                x.setMemberPurchaseTypeStr(x.getType().getDesc());
            }).toList();
        }
        result.setRecords(memberPurchaseHistoryVos);
        return result;
    }

    /**
     * 会员充值订单列表导出
     *
     * @param query 会员充值订单
     * @return 导出id
     */
    @Override
    public Long export(MemberPurchaseQueryDTO query) {
        SecureUser secureUser = ISecurity.userMust();
        DataExportRecordDTO dto = new DataExportRecordDTO();

        dto.setExportUserId(secureUser.getId())
                .setDataType(ExportDataType.MEMBER_RECORDER)
                .setShopId(secureUser.getShopId())
                .setUserPhone(secureUser.getMobile());
        Long exportRecord = dataExportRecordRpcService.saveExportRecord(dto);
        MemberPurchaseHistoryExportTask task = new MemberPurchaseHistoryExportTask(exportRecord,
                dataExportRecordRpcService,
                query, this, pigeonChatStatisticsRpcService);
        globalExecutor.execute(task);

        return exportRecord;
    }
}
