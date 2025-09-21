package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.BalanceHistoryOperatorType;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.IManualTransaction;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.user.api.model.dto.BalanceChangeDTO;
import com.medusa.gruul.user.service.model.dto.UserBalanceHistoryDTO;
import com.medusa.gruul.user.service.model.vo.UserBalanceHistoryQueryVo;
import com.medusa.gruul.user.service.mp.entity.UserAccount;
import com.medusa.gruul.user.service.mp.entity.UserBalanceHistory;
import com.medusa.gruul.user.service.mp.service.IUserAccountService;
import com.medusa.gruul.user.service.mp.service.IUserBalanceHistoryService;
import com.medusa.gruul.user.service.service.IUserBalanceHistoryManageService;
import com.medusa.gruul.user.service.task.UserBalanceHistoryExportTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;


/**
 * 用户余额流水管理服务实现类
 *
 * @author jipeng
 * date 2024/2/2
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserBalanceHistoryManageServiceImpl implements IUserBalanceHistoryManageService {


    public static final String SYSTEM_NAME = "系统";
    public static final String ADD_PREFIX = "+";
    public static final String MINUS_PREFIX = "-";
    private final IUserBalanceHistoryService userBalanceHistoryService;
    private final IUserAccountService userAccountService;
    private final Executor globalExecutor;
    private final DataExportRecordRpcService dataExportRecordRpcService;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private UaaRpcService uaaRpcService;

    @Autowired
    @Lazy
    public void setUaaRpcService(UaaRpcService uaaRpcService) {
        this.uaaRpcService = uaaRpcService;
    }

    /**
     * 异步保存用户储值流水信息
     *
     * @param userBalanceHistoryDTO 用户储值流水信息
     */
    @Override
    public void asyncSaveUserBalanceHistory(BalanceChangeDTO userBalanceHistoryDTO, Long sysSeqNo, Long personSeqNo) {
        //todo 哪里有异步？
        IManualTransaction.afterCommit(
                () -> {
                    try {
                        saveUserBalanceHistory(userBalanceHistoryDTO, sysSeqNo, personSeqNo);
                    } catch (Exception e) {
                        log.error("异步保存用户余额流水失败", e);
                    }
                }
        );

    }

    /**
     * 保存用户储值流水信息
     *
     * @param mqDto 用户储值流水信息
     */
    public void saveUserBalanceHistory(BalanceChangeDTO mqDto, Long sysSeqNo, Long personSeqNo) {

        DataChangeMessage systemDataChangeMessage = mqDto.getSystemDataChangeMessage();
        DataChangeMessage personDataChangeMessage = mqDto.getPersonDataChangeMessage();

        if (Objects.nonNull(systemDataChangeMessage)) {
            saveSystemData(systemDataChangeMessage, mqDto.getPersonDataChangeMessage(), sysSeqNo);
        }

        if (Objects.nonNull(personDataChangeMessage)) {
            savePersonData(personDataChangeMessage, personSeqNo);
        }
    }

    /**
     * 用户储值流水列表查询
     *
     * @param queryVo 用户储值流水列表查询参数
     * @return 用户储值流水列表
     */
    @Override
    public Page<UserBalanceHistoryDTO> list(UserBalanceHistoryQueryVo queryVo) {
        Page<UserBalanceHistory> pageList = userBalanceHistoryService.queryList(queryVo);
        List<UserBalanceHistory> records = pageList.getRecords();
        Page<UserBalanceHistoryDTO> result = new Page<>();
        BeanUtil.copyProperties(pageList, result);
        if (CollectionUtil.isEmpty(records)) {
            return result;
        }
        List<UserBalanceHistoryDTO> dtos = BeanUtil.copyToList(records,
                UserBalanceHistoryDTO.class);
        Set<Long> userIds = dtos.stream().map(UserBalanceHistoryDTO::getOperatorUserId)
                .filter(operatorUserId ->
                        operatorUserId != CommonPool.NUMBER_ZERO).collect(Collectors.toSet());

        Map<Long, UserInfoVO> userInfoMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(userIds)) {
            userInfoMap = uaaRpcService.getUserDataBatchByUserIds(
                    userIds);


        }
        Map<Long, UserInfoVO> finalUserInfoMap = userInfoMap;
        dtos = dtos.stream().peek(x -> {
            x.setOperatorTypeStr(x.getOperatorType().getMsg());

            if (ChangeType.INCREASE.equals(x.getChangeType())) {
                x.setAmountStr(ADD_PREFIX + UserBalanceHistoryExportTask.dealPrice(x.getAmount()));
            } else {
                x.setAmountStr(MINUS_PREFIX + UserBalanceHistoryExportTask.dealPrice(x.getAmount()));
            }
            x.setAfterAmountStr(UserBalanceHistoryExportTask.dealPrice(x.getAfterAmount()));
            if (Long.valueOf(CommonPool.NUMBER_ZERO).equals(x.getOperatorUserId())) {
                x.setOperatorUserNickName(SYSTEM_NAME);
            } else {
                UserInfoVO userInfoVO = finalUserInfoMap.get(x.getOperatorUserId());
                if (Objects.nonNull(userInfoVO)) {
                    x.setOperatorUserNickName(
                            StringUtils.isEmpty(userInfoVO.getNickname()) ? userInfoVO.getUsername()
                                    : userInfoVO.getNickname());
                }
            }
        }).collect(Collectors.toList());
        result.setRecords(dtos);
        return result;
    }

    /**
     * 用户储值流水修改备注
     *
     * @param ids    ids
     * @param remark remark
     */
    @Override
    public void remark(List<Long> ids, String remark) {
        userBalanceHistoryService.remark(ids, remark);
    }

    /**
     * 导出储值记录
     *
     * @param queryVo 储值vo
     * @return id
     */
    @Override
    public Long export(UserBalanceHistoryQueryVo queryVo) {
        SecureUser secureUser = ISecurity.userMust();
        DataExportRecordDTO dto = new DataExportRecordDTO();

        dto.setExportUserId(secureUser.getId())
                .setDataType(ExportDataType.STORED_VALUE_FLOW)
                .setShopId(secureUser.getShopId())
                .setUserPhone(secureUser.getMobile());
        Long exportRecord = dataExportRecordRpcService.saveExportRecord(dto);
        UserBalanceHistoryExportTask task = new UserBalanceHistoryExportTask(
                exportRecord, dataExportRecordRpcService, pigeonChatStatisticsRpcService, this, queryVo);
        globalExecutor.execute(task);
        return exportRecord;
    }

    /**
     * 导出储值记录
     *
     * @param systemDataChangeMessage 系统数据
     * @param personDataChangeMessage 用户数据
     */
    public UserBalanceHistory saveSystemData(DataChangeMessage systemDataChangeMessage,
                                             DataChangeMessage personDataChangeMessage, Long seqNo) {
        Long userId = systemDataChangeMessage.getUserId();
        if (Objects.isNull(userId)) {
            throw SystemCode.PARAM_MISS.exception();
        }

        //检测用户是否存在
        UserAccount account = userAccountService.lambdaQuery()
                .eq(UserAccount::getUserId, userId).one();
        SystemCode.DATA_NOT_EXIST.trueThrow(Objects.isNull(account));
        Long seq = Objects.nonNull(seqNo) ? seqNo : IdUtil.getSnowflakeNextId();
        Long afterAmount = account.getBalance();
        if (BalanceHistoryOperatorType.SYSTEM_GIFT.equals(systemDataChangeMessage.getOperatorType())) {
            afterAmount = account.getBalance() - personDataChangeMessage.getValue();
        }
        UserBalanceHistory entry = new UserBalanceHistory();
        entry.setNo(seq)
                .setUserId(userId)
                .setUserPhone(account.getUserPhone())
                .setUserNickName(account.getUserNickname())
                .setOperatorType(systemDataChangeMessage.getOperatorType())
                .setAmount(systemDataChangeMessage.getValue())
                .setAfterAmount(afterAmount)
                .setChangeType(systemDataChangeMessage.getChangeType())
                .setOrderNo(systemDataChangeMessage.getOrderNo())
                .setOperatorUserId(systemDataChangeMessage.getOperatorUserId());
        userBalanceHistoryService.save(entry);
        return entry;
    }

    public UserBalanceHistory savePersonData(DataChangeMessage personChangeData, Long seqNo) {
        Long userId = personChangeData.getUserId();
        if (Objects.isNull(userId)) {
            throw SystemCode.PARAM_MISS.exception();
        }

        //检测用户是否存在
        UserAccount account = userAccountService.lambdaQuery()
                .eq(UserAccount::getUserId, userId)
                .one();
        SystemCode.DATA_NOT_EXIST.trueThrow(Objects.isNull(account));
        Long seq = Objects.nonNull(seqNo) ? seqNo : IdUtil.getSnowflakeNextId();
        UserBalanceHistory entry = new UserBalanceHistory();
        entry.setNo(seq)
                .setUserId(userId)
                .setUserPhone(account.getUserPhone())
                .setUserNickName(account.getUserNickname())
                .setOperatorType(personChangeData.getOperatorType())
                .setAmount(personChangeData.getValue())
                .setAfterAmount(Objects.nonNull(personChangeData.getAfterValue()) ? personChangeData.getAfterValue() : account.getBalance())
                .setChangeType(personChangeData.getChangeType())
                .setOrderNo(personChangeData.getOrderNo())
                .setOperatorUserId(personChangeData.getOperatorUserId());
        userBalanceHistoryService.save(entry);
        return entry;
    }

}
