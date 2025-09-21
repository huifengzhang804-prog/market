package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.user.api.constant.UserConstant;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.enums.integral.GainIntegralType;
import com.medusa.gruul.user.api.model.dto.integral.IntegralChangeDTO;
import com.medusa.gruul.user.api.model.vo.CurrentMemberVO;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import com.medusa.gruul.user.service.model.dto.UserIntegralChangeDTO;
import com.medusa.gruul.user.service.mp.entity.UserAccount;
import com.medusa.gruul.user.service.mp.entity.UserIntegralDetail;
import com.medusa.gruul.user.service.mp.service.IUserAccountService;
import com.medusa.gruul.user.service.mp.service.IUserIntegralDetailService;
import com.medusa.gruul.user.service.service.MemberCardService;
import com.medusa.gruul.user.service.service.UserIntegralService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author shishuqian
 * date 2023/2/8
 * time 11:46
 **/

@Service
@RequiredArgsConstructor
public class UserIntegralServiceImpl implements UserIntegralService {

    private final MemberCardService memberCardService;

    private final IUserAccountService userAccountService;

    private final IUserIntegralDetailService iUserIntegralDetailService;

    @Override
    public Long getIntegralTotalByUserId(Long userId) {
        UserAccount user = this.userAccountService.lambdaQuery()
                .eq(UserAccount::getUserId, userId)
                .one();
        if (user == null) {
            throw new GlobalException("用户不存在");
        }

        return user.getIntegralTotal();
    }

    /**
     * ------加积分-------
     * 1、判断用户积分是否加倍
     * 加倍：获取用户会员权益数据 获取用户积分加倍倍数，然后计算最终积分
     * 不加倍：直接加积分
     * 2、生成积分明细
     * ------减积分---------
     * 1、生成积分明细
     * notion: 积分变化详情的 particulars 字段
     * 下单/取消订单： 订单号：JF2333212345678
     * 每日登录/签到/分享： +3倍
     */
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = UserConstant.USER_INTEGRAL_TOTAL_KEY, key = "#integralChangeDTO.dataChangeMessage.userId")
    @Override
    public void handlerIntegralChangeMessage(IntegralChangeDTO integralChangeDTO) {

        DataChangeMessage dataChangeMessage = integralChangeDTO.getDataChangeMessage();
        Long userId = dataChangeMessage.getUserId();
        //积分变化类型： 增加/减少
        ChangeType changeType = dataChangeMessage.getChangeType();
        //转为GainIntegralType
        GainIntegralType gainIntegralType = GainIntegralType.valueOf(dataChangeMessage.getExtendInfo());

        String descText = null;
        //下单详情描述
        if (gainIntegralType == GainIntegralType.ORDER_CONSUMPTION ||
                gainIntegralType == GainIntegralType.ORDER_CANCEL) {
            //如果积分变化类型为订单消费和订单取消
            descText = "订单号：" + integralChangeDTO.getNo();
        }


        //要变化积分的原始值
        Long gainValue = dataChangeMessage.getValue();

        if (changeType == ChangeType.INCREASE) {
            //增加积分，生成明细数据

            //判断该积分获取是否需要加倍
            if (gainIntegralType.getIsDouble()) {
                //获取用户会员卡数据
                MemberAggregationInfoVO memberCentre = this.memberCardService.getMemberCentre(userId);
                CurrentMemberVO currentMemberVO = memberCentre.getCurrentMemberVO();
                Map<RightsType, List<RelevancyRightsVO>> relevancyRights = currentMemberVO.getRelevancyRights();
                //用户会员的积分加倍权益
                Optional<List<RelevancyRightsVO>> relevancyRightsVO = Optional.ofNullable(relevancyRights.get(RightsType.INTEGRAL_MULTIPLE));

                if (relevancyRightsVO.isPresent()) {
                    //有积分加倍权益
                    //积分加倍倍数
                    RelevancyRightsVO rights = relevancyRightsVO.get().get(0);
                    Long extendValue = rights.getExtendValue();
                    if (extendValue != null && extendValue != 0L) {
                        //积分翻倍后获得的积分
                        gainValue = gainValue * (extendValue / CommonPool.UNIT_CONVERSION_HUNDRED);
                        //积分详情描述
                        descText = StrUtil.format("+{}倍", extendValue / CommonPool.UNIT_CONVERSION_HUNDRED);
                    }
                }
            }

            //增加积分
            this.userAccountService.lambdaUpdate()
                    .eq(UserAccount::getUserId, userId)
                    //更新积分
                    .setSql(
                            StrUtil.format(UserConstant.USER_ACCOUNT_INTEGRAL_TOTAL_SQL_TEMPLATE, gainValue)
                    )
                    .update();

        }


        //积分明细入库
        UserIntegralDetail userIntegralDetail = new UserIntegralDetail();
        userIntegralDetail.setUserId(dataChangeMessage.getUserId())
                .setGainIntegralType(gainIntegralType)
                .setChangeType(changeType)
                .setVariationIntegral(gainValue)
                .setParticulars(descText)
                .setClear(gainIntegralType == GainIntegralType.INTEGRAL_CLEAR);
        //设置积分明细的当前积分
        UserAccount userAccount = this.userAccountService.lambdaQuery()
                .eq(UserAccount::getUserId, userId)
                .one();
        userIntegralDetail.setCurrentIntegral(userAccount.getIntegralTotal());

        //入库
        this.iUserIntegralDetailService.save(userIntegralDetail);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @Redisson(value = UserConstant.USER_INTEGRAL_TOTAL_KEY, key = "#dto.userId")
    public boolean changeIntegralBySystem(UserIntegralChangeDTO dto) {
        //变化类型
        ChangeType changeType = dto.getChangeType();

        DataChangeMessage dataChangeMessage = new DataChangeMessage();
        dataChangeMessage.setUserId(dto.getUserId());
        dataChangeMessage.setValue(dto.getIntegral());
        dataChangeMessage.setChangeType(changeType);

        if (changeType == ChangeType.INCREASE) {
            //增加积分
            dataChangeMessage.setExtendInfo(GainIntegralType.SYSTEM_RECHARGE.name());
            IntegralChangeDTO integralChangeDTO = new IntegralChangeDTO();
            integralChangeDTO.setNo(null)
                    .setDataChangeMessage(dataChangeMessage);
            //加积分处理
            this.handlerIntegralChangeMessage(integralChangeDTO);

            return true;
        } else {
            //减少积分

            dataChangeMessage.setExtendInfo(GainIntegralType.SYSTEM_DEDUCT.name());
            IntegralChangeDTO integralChangeDTO = new IntegralChangeDTO();
            integralChangeDTO.setNo(null)
                    .setDataChangeMessage(dataChangeMessage);

            UserAccount userAccount = this.userAccountService.lambdaQuery()
                    .eq(UserAccount::getUserId, dto.getUserId()).one();
            if (userAccount == null) {
                throw new GlobalException("当前用户信息不存在");
            }
            //用户剩余的积分
            Long integralTotal = userAccount.getIntegralTotal();
            integralTotal = integralTotal == null ? 0L : integralTotal;

            //积分变化值
            Long changeAmount = dto.getIntegral();
            changeAmount = changeAmount > integralTotal ? integralTotal : changeAmount;

            boolean success = this.userAccountService.lambdaUpdate()
                    .eq(UserAccount::getUserId, dto.getUserId())
                    //更新积分
                    .setSql(
                            StrUtil.format(UserConstant.USER_ACCOUNT_INTEGRAL_TOTAL_SQL_TEMPLATE, -changeAmount)
                    )
                    .update();

            if (success) {
                //减积分处理
                this.handlerIntegralChangeMessage(integralChangeDTO);

            }
            return success;

        }


    }


    @Override
    public void clearUserIntegralBySystemTime() {

        //1、查询所有用户
        List<UserAccount> userList = this.userAccountService.list();

        //TODO 多线程
        //2、遍历每一个用户，进行积分清空
        for (UserAccount userAccount : userList) {

            //实时查
            UserAccount one = this.userAccountService.lambdaQuery()
                    .eq(UserAccount::getUserId, userAccount.getUserId())
                    .one();

            if (one != null) {
                boolean success = this.userAccountService.lambdaUpdate()
                        .eq(UserAccount::getUserId, one.getUserId())
                        .set(UserAccount::getIntegralTotal, 0L)
                        .update();

                //生成减积分记录
                if (success) {

                    DataChangeMessage dataChangeMessage = new DataChangeMessage();
                    dataChangeMessage.setUserId(one.getUserId());
                    dataChangeMessage.setValue(one.getIntegralTotal());
                    dataChangeMessage.setChangeType(ChangeType.REDUCE);
                    dataChangeMessage.setExtendInfo(GainIntegralType.INTEGRAL_CLEAR.name());

                    IntegralChangeDTO integralChangeDTO = new IntegralChangeDTO();
                    integralChangeDTO.setNo(null)
                            .setDataChangeMessage(dataChangeMessage);
                    //减积分处理
                    SpringUtil.getBean(UserIntegralServiceImpl.class).handlerIntegralChangeMessage(integralChangeDTO);
                }

            }

        }
    }
}
