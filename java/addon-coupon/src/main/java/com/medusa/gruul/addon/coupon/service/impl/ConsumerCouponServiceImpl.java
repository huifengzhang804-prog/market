package com.medusa.gruul.addon.coupon.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.coupon.model.CouponConstant;
import com.medusa.gruul.addon.coupon.model.CouponErrorCode;
import com.medusa.gruul.addon.coupon.model.dto.*;
import com.medusa.gruul.addon.coupon.model.enums.CouponCollectionType;
import com.medusa.gruul.addon.coupon.model.enums.CouponStatus;
import com.medusa.gruul.addon.coupon.model.vo.CouponUserVO;
import com.medusa.gruul.addon.coupon.model.vo.CouponVO;
import com.medusa.gruul.addon.coupon.mp.entity.Coupon;
import com.medusa.gruul.addon.coupon.mp.entity.CouponCalculate;
import com.medusa.gruul.addon.coupon.mp.entity.CouponUser;
import com.medusa.gruul.addon.coupon.mp.service.ICouponCalculateService;
import com.medusa.gruul.addon.coupon.mp.service.ICouponService;
import com.medusa.gruul.addon.coupon.mp.service.ICouponUserService;
import com.medusa.gruul.addon.coupon.service.ConsumerCouponService;
import com.medusa.gruul.addon.coupon.service.CouponPlusService;
import com.medusa.gruul.addon.coupon.task.CouponUserExportTask;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/11/3
 */
@Service
@RequiredArgsConstructor
public class ConsumerCouponServiceImpl implements ConsumerCouponService {

    private final ShopRpcService shopRpcService;
    private final ICouponService couponService;
    private final ICouponUserService couponUserService;
    private final CouponPlusService couponPlusService;
    private final ICouponCalculateService couponCalculateService;
    private final DataExportRecordRpcService dataExportRecordRpcService;
    private final Executor globalExecutor;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final UaaRpcService uaaRpcService;


    @Override
    public IPage<CouponVO> consumerCouponPage(Option<Long> userIdOpt, ConsumerCouponQueryDTO query) {
        query.validParam();
        IPage<CouponVO> page = couponService.consumerCouponPage(userIdOpt.getOrNull(), query);
        List<CouponVO> coupons = page.getRecords();
        //当查询平台券 或 指定了店铺id 都不用去查询店铺名称
        if (query.getIsPlatform() || query.getShopId() != null || CollUtil.isEmpty(coupons)) {
            return page;
        }
        Map<Long, String> shopNameMap = shopRpcService.getShopInfoByShopIdList(
                        coupons.stream().map(CouponVO::getShopId).collect(Collectors.toSet())
                ).stream()
                .collect(Collectors.toMap(ShopInfoVO::getId, ShopInfoVO::getName));
        coupons.forEach(coupon -> coupon.setShopName(shopNameMap.get(coupon.getShopId())));
        return page;
    }

    @Override
    @Redisson(value = CouponConstant.COUPON_USER_COLLECT_LOCK, key = "#userId+':'+#shopId+':'+#couponId")
    public Boolean collectCoupon(Long userId, Long shopId, Long couponId) {
        //查询优惠券详情
        Coupon coupon = couponPlusService.getCoupon(shopId, couponId).getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST));
        //校验是否不可用
        if (coupon.getStatus() == CouponStatus.BANED ||
                CouponStatus.SHOP_BANED.equals(coupon.getStatus())) {
            //平台下架 或者店铺下架 都不可用
            throw new GlobalException(CouponErrorCode.COUPON_INVALID, "当前优惠券不可用");
        }
        //检查是否库存不足
        Long stock = coupon.getStock();
        Integer num = CommonPool.NUMBER_ONE;
        if (stock < num) {
            throw new GlobalException(CouponErrorCode.COUPON_OUT_STOCK, ("优惠券库存不足"));
        }
        //校验已领取的优惠券数量是否超额
        Long count = couponUserService.lambdaQuery()
                .eq(CouponUser::getUserId, userId)
                .eq(CouponUser::getShopId, shopId)
                .eq(CouponUser::getCouponId, couponId)
                .count();
        if (count >= coupon.getLimit()) {
            throw new GlobalException(CouponErrorCode.COUPON_OUT_LIMIT, "超过了每人限领数量");
        }
        // redis 减库存 -> 异步db减库存
        coupon = couponPlusService.couponStockReduce(shopId, couponId, num);

        // 领券用户信息
        SecureUser<Object> couponSecureUser = ISecurity.userMust();
        CouponUserInfoDTO userBaseInfo = new CouponUserInfoDTO()
                .setCouponUserInfo(new CouponUserInfoDTO.UserInfo().setUserId(couponSecureUser.getId()).setMobile(couponSecureUser.getMobile()).setNickname(couponSecureUser.getNickname()));
        userBaseInfo.setCollectType(CouponCollectionType.SELF_COLLECTION);

        boolean save = couponUserService.save(coupon.newCouponUser(userBaseInfo, couponPlusService::getProductIds));
        if (!save) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED);
        }
        //这里的返回值 用于前端判断用户是否可以继续领取该优惠券
        // 领取的限制 减去 已经领取的数量 这里判断大于1 只有大于1 才表示用户可以继续领取
        return coupon.getLimit() - count > 1;
    }

    @Override
    public List<CouponVO> platformCouponAvailable(Long userId) {
        //满折
        //满减
        //无门槛 折扣/优惠
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<CouponVO> orderShopCouponPage(Long userId, OrderCouponPageDTO orderCouponPage) {
        Long shopId = orderCouponPage.getShopId();
        return couponCalculateService.todo(
                orderCouponPage.getProductAmounts()
                        .stream()
                        .map(productAmount -> new CouponCalculate().setShopId(shopId).setProductId(productAmount.getProductId()).setAmount(productAmount.getAmount()))
                        .collect(Collectors.toList()),
                bid -> couponService.orderShopCouponPage(bid, userId, orderCouponPage)
        );

    }

    @Override
    public IPage<CouponVO> productShopCouponPage(ProductCouponPageDTO query) {
        return couponService.productShopCouponPage(query, ISecurity.userOpt().map(SecureUser::getId).getOrNull());
    }

    /**
     * 用券记录
     *
     * @param query 查询参数
     * @return 分页查询结果
     */
    @Override
    public IPage<CouponUserVO> useRecord(CouponUserDTO query) {

        IPage<CouponUserVO> page = couponUserService.useRecords(query);

        if (CollUtil.isEmpty(page.getRecords())) {
            return page;
        }
        // 存缓存吗, 应该不用

        // 优惠券用户信息(昵称)
        Map<Long, UserInfoVO> userInfoMap = uaaRpcService.getUserDataBatchByUserIds(page.getRecords().stream().map(CouponUserVO::getUserId).collect(Collectors.toSet()));

        page.getRecords().forEach(couponUser -> {
                    couponUser
                            .setUserNickname(userInfoMap.getOrDefault(couponUser.getUserId(), new UserInfoVO()).getNickname())
                            .setQueryStatus(couponUser.queryStatus(couponUser.getUsed(), couponUser.getEndDate()))
                            .setCollectTypeText(null == couponUser.getCollectType() ? "" : couponUser.getCollectType().getText())
                            .setTypeDescription(couponUser.getType().getUserRecordDesc(couponUser.getRequiredAmount(), couponUser.getAmount(), couponUser.getDiscount()));
                    couponUser.setQueryStatusText(couponUser.getQueryStatus().getText());
                    couponUser.setParValue(couponUser.parValue());
                }

        );

        return page;

    }

    /**
     * 用券记录列表导出
     *
     * @param query 用券记录查询参数
     * @return 导出id
     */
    @Override
    public Long export(CouponUserDTO query) {
        SecureUser<Object> secureUser = ISecurity.userMust();
        DataExportRecordDTO dto = new DataExportRecordDTO();

        dto.setExportUserId(secureUser.getId())
                .setDataType(ExportDataType.COUPON_RECORD)
                .setShopId(secureUser.getShopId())
                .setUserPhone(secureUser.getMobile());
        Long exportRecordId = dataExportRecordRpcService.saveExportRecord(dto);
        CouponUserExportTask task = new CouponUserExportTask(exportRecordId, query, this, dataExportRecordRpcService, pigeonChatStatisticsRpcService);
        globalExecutor.execute(task);

        return exportRecordId;
    }


}
