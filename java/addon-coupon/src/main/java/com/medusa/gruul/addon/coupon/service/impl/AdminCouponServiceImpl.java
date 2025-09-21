package com.medusa.gruul.addon.coupon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.medusa.gruul.addon.coupon.model.CouponConstant;
import com.medusa.gruul.addon.coupon.model.CouponErrorCode;
import com.medusa.gruul.addon.coupon.model.dto.*;
import com.medusa.gruul.addon.coupon.model.enums.*;
import com.medusa.gruul.addon.coupon.mp.entity.Coupon;
import com.medusa.gruul.addon.coupon.mp.entity.CouponProduct;
import com.medusa.gruul.addon.coupon.mp.entity.CouponUser;
import com.medusa.gruul.addon.coupon.mp.service.ICouponProductService;
import com.medusa.gruul.addon.coupon.mp.service.ICouponService;
import com.medusa.gruul.addon.coupon.mp.service.ICouponUserService;
import com.medusa.gruul.addon.coupon.service.AdminCouponService;
import com.medusa.gruul.addon.coupon.service.CouponPlusService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author 张治保 date 2022/11/2
 */
@Service
@RequiredArgsConstructor
public class AdminCouponServiceImpl implements AdminCouponService {

    private final ShopRpcService shopRpcService;
    private final ICouponService couponService;
    private final ICouponUserService couponUserService;
    private final ICouponProductService couponProductService;
    private final CouponPlusService couponPlusService;
    private final UaaRpcService uaaRpcService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void newCoupon(Long shopId, CouponDTO coupon) {
        coupon.validParam(Boolean.FALSE);
        Coupon couponEntity = coupon.toCouponEntity(shopId);
        boolean success = couponService.save(couponEntity);
        if (!success) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED);
        }
        Set<Long> productIds = coupon.getProductIds();
        if (CollUtil.isEmpty(productIds)) {
            return;
        }
        Long couponEntityId = couponEntity.getId();
        //保存优惠券对应的产品
        couponProductService.saveBatch(
                productIds.stream()
                        .map(productId -> new CouponProduct().setShopId(shopId).setCouponId(couponEntityId)
                                .setProductId(productId))
                        .collect(Collectors.toList())
        );
    }

    /**
     * 分页查询优惠券列表
     *
     * @param shopIdOpt  可能为空的店铺id null 表示平台   非null 为店铺id
     * @param queryParam 分页查询条件
     * @return 优惠券列表
     */
    @Override
    public IPage<Coupon> couponPage(Option<Long> shopIdOpt, CouponQueryDTO queryParam) {
        IPage<Coupon> page = couponService.couponPage(shopIdOpt.getOrNull(), queryParam);
        List<Coupon> coupons = page.getRecords();
        if (CollUtil.isEmpty(coupons)) {
            return page;
        }
        for (Coupon coupon : coupons) {
            coupon.getStatus().getDescCheckFun().accept(coupon);
        }

        shopIdOpt.onEmpty(
                () -> {
                    Map<Long, String> shopNameMap = shopRpcService.getShopInfoByShopIdList(
                                    coupons.stream().map(Coupon::getShopId).collect(Collectors.toSet()
                                    )
                            ).stream()
                            .collect(Collectors.toMap(ShopInfoVO::getId, ShopInfoVO::getName));
                    coupons.forEach(
                            coupon -> coupon.setShopName(shopNameMap.get(coupon.getShopId()))
                    );
                }
        );
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = CouponConstant.COUPON_EDIT_LOCK_KEY, key = "#shopId+':'+#couponId")
    public void editCoupon(Long shopId, Long couponId, CouponDTO coupon) {
        coupon.validParam(Boolean.FALSE);
        Coupon couponEntity = this.getAndCheckCoupon(shopId, couponId, Boolean.FALSE);

        if (!couponEntity.notStated()) {
            //不是未开始的
            throw CouponErrorEnum.COUPON_STATE_ERROR.exception();
        }
        CouponProductType currentProductType = coupon.getProductType();
        CouponProductType preProductType = couponEntity.getProductType();

        couponEntity.setEffectType(coupon.getEffectType());
        couponEntity.setDays(coupon.getDays())
                .setNum(coupon.getNum())
                .setStock(coupon.getNum())
                .setLimit(coupon.getLimit())
                .setShopId(shopId)
                .setType(coupon.getType())
                .setName(StrUtil.trim(coupon.getName()))
                .setRequiredAmount(coupon.getRequiredAmount())
                .setAmount(coupon.getAmount())
                .setDiscount(coupon.getDiscount())
                .setProductType(currentProductType)
                .setStartDate(coupon.getStartDate())
                .setEndDate(coupon.getEndDate());
        //缓存双删
        RedisUtil.doubleDeletion(
                () -> {
                    boolean update = couponService.updateById(couponEntity);
                    if (!update) {
                        throw new GlobalException(SystemCode.DATA_UPDATE_FAILED);
                    }
                },
                CouponConstant.COUPON_CACHE_KEY, shopId, couponId
        );
        //保存 优惠券商品关联表
        this.updateCouponProducts(shopId, couponId, preProductType, currentProductType,
                coupon.getProductIds());

    }

    private void updateCouponProducts(Long shopId, Long couponId, CouponProductType preProductType,
                                      CouponProductType currentProductType, Set<Long> currentProductIds) {
        Boolean preIsAssigned = preProductType.getIsAssigned();
        Boolean currentIsAssigned = currentProductType.getIsAssigned();

        // 之前非指定 当前也是非指定 跳过
        if (!preIsAssigned && !currentIsAssigned) {
            return;
        }
        //之前指定 当前非指定 删除之前指定的数据
        if (preIsAssigned && !currentIsAssigned) {
            couponProductService.lambdaUpdate().eq(CouponProduct::getShopId, shopId)
                    .eq(CouponProduct::getCouponId, couponId).remove();
            return;
        }
        //剩下的都是当前指定过的数据
        //之前非指定 当前指定 则新增商品指定关系
        if (!preIsAssigned) {
            List<CouponProduct> couponProducts = currentProductIds.stream().map(
                    productId -> new CouponProduct().setCouponId(couponId).setShopId(shopId)
                            .setProductId(productId)
            ).collect(Collectors.toList());
            couponProductService.saveBatch(couponProducts);
            return;
        }
        //之前指定 当前指定 查询之前的商品
        Set<Long> preProductIds = couponProductService.lambdaQuery().select(CouponProduct::getProductId)
                .eq(CouponProduct::getShopId, shopId)
                .eq(CouponProduct::getCouponId, couponId)
                .list().stream().map(CouponProduct::getProductId).collect(Collectors.toSet());

        //取之前的商品id列表 与 当前商品id列表 交集
        Collection<Long> interProductIds = CollUtil.intersection(preProductIds, currentProductIds);
        preProductIds.removeAll(interProductIds);
        if (CollUtil.isNotEmpty(preProductIds)) {
            couponProductService.lambdaUpdate().eq(CouponProduct::getShopId, shopId)
                    .eq(CouponProduct::getCouponId, couponId).in(CouponProduct::getProductId, preProductIds)
                    .remove();
        }
        currentProductIds.removeAll(interProductIds);
        List<CouponProduct> couponProducts = currentProductIds.stream().map(
                productId -> new CouponProduct().setCouponId(couponId).setShopId(shopId)
                        .setProductId(productId)
        ).collect(Collectors.toList());
        couponProductService.saveBatch(couponProducts);
    }

    /**
     * 检查并获取优惠券entity
     *
     * @param shopId      店铺id
     * @param couponId    优惠券id
     * @param needWorking 是否需要正在进行中的优惠券
     * @return 优惠券entity
     */
    private Coupon getAndCheckCoupon(Long shopId, Long couponId, boolean needWorking) {
        Coupon couponEntity = couponPlusService.getCoupon(shopId, couponId)
                .getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST));
        //检查是否处于生效期
        LocalDate now = LocalDate.now();
        Boolean isPeriod = couponEntity.getEffectType().getIsPeriod();

        boolean worked = isPeriod && !now.isBefore(couponEntity.getStartDate()) && !now.isAfter(
                couponEntity.getEndDate());
        //是否正在进行中
        boolean working = worked || !isPeriod;

        //需要优惠券生效
        if (needWorking) {
            if (working) {
                return couponEntity;
            }
            throw new GlobalException(CouponErrorCode.COUPON_NEED_INVALID_EDIT, "编辑方式有误");
        }
        //不需优惠券生效 但当前优惠券已生效
        if (working) {
            throw new GlobalException(CouponErrorCode.COUPON_NEED_VALID_EDIT, "编辑方式有误");
        }
        return couponEntity;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = CouponConstant.COUPON_EDIT_LOCK_KEY, key = "#shopId+':'+#couponId")
    public void editValidCoupon(Long shopId, Long couponId, CouponWorkingEditDTO coupon) {
        coupon.validParam(Boolean.FALSE);
        Coupon dbCoupon = couponService.getBaseMapper().selectById(couponId);
        if (Objects.isNull(dbCoupon)) {
            throw SystemCode.NOT_FOUND.exception();
        }
        if (!dbCoupon.notStated()) {
            //优惠券不是未开始的状态
            throw CouponErrorEnum.COUPON_STATE_ERROR.exception();
        }

        EffectType effectType = dbCoupon.getEffectType();
        //立即生效 生效天数校验
        if (EffectType.IMMEDIATELY == effectType) {
            Integer days = coupon.getDays();
            if (days == null) {
                throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
            }
            dbCoupon.setDays(days);
        }
        //固定时间生效 生效结束时间校验
        if (EffectType.PERIOD == effectType) {
            LocalDate endDate = coupon.getEndDate();
            if (endDate.isBefore(dbCoupon.getStartDate())) {
                throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
            }
            dbCoupon.setEndDate(endDate);
        }

        CouponProductType preProductType = dbCoupon.getProductType();
        CouponProductType currentProductType = coupon.getProductType();
        dbCoupon.setName(StrUtil.trim(coupon.getName()))

                .setProductType(currentProductType);

        RedisUtil.doubleDeletion(
                () -> {
                    boolean update = couponService.updateById(dbCoupon);
                    if (!update) {
                        throw new GlobalException(SystemCode.DATA_UPDATE_FAILED);
                    }
                },
                CouponConstant.COUPON_CACHE_KEY, shopId, couponId
        );
        this.updateCouponProducts(shopId, couponId, preProductType, currentProductType,
                coupon.getProductIds());
    }

    /**
     * 删除：只有处于【未开始、已结束、违规下架、已下架】的活动才能删除
     *
     * @param shopId    店铺id
     * @param couponIds 优惠券id列表
     */
    @Override
    public void deleteShopCouponBatch(Long shopId, Set<Long> couponIds) {
        List<Coupon> coupons = couponService.getBaseMapper().selectBatchIds(couponIds);
        if (CollectionUtils.isEmpty(coupons)) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        Long currentShopId = ISystem.shopIdMust();
        coupons = coupons.stream().filter(coupon -> {

            if (!coupon.getShopId().equals(currentShopId)) {
                //优惠券不属于当前店铺的
                return Boolean.FALSE;
            }
            if (coupon.notStated() ||
                    coupon.finished() ||
                    coupon.platformIllegalOffShelf() ||
                    coupon.shopOffShelf()) {
                //只有处于【未开始、已结束、违规下架、已下架】的活动才能删除
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }).toList();
        if (CollectionUtils.isEmpty(coupons)) {
            throw CouponErrorEnum.CAN_NOT_REMOVE.exception();
        }
        couponIds = coupons.stream().map(Coupon::getId).collect(Collectors.toSet());
        Set<String> couponCacheKeys = coupons.stream()
                .map(coupon -> RedisUtil.key(CouponConstant.COUPON_CACHE_KEY, shopId, coupon.getId()))
                .collect(Collectors.toSet());
        Set<Long> finalCouponIds = couponIds;
        RedisUtil.doubleDeletion(
                () -> {
                    boolean remove = couponService.lambdaUpdate()
                            .set(Coupon::getDeleted, Boolean.TRUE)
                            .eq(Coupon::getShopId, shopId)
                            .in(Coupon::getId, finalCouponIds)
                            .update();
                    if (!remove) {
                        throw new GlobalException(SystemCode.DATA_DELETE_FAILED);
                    }
                },
                () -> RedisUtil.delete(couponCacheKeys)
        );

    }


    /**
     * 平台端违规下架优惠券
     *
     * @param shopCoupons 店铺id与优惠券id列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void banCouponBatch(List<ShopCouponMapDTO> shopCoupons) {
        Map<Long, ShopCouponMapDTO> map = shopCoupons.stream()
                .collect(Collectors.toMap(ShopCouponMapDTO::getCouponId, x -> x,
                        (existing, replacement) -> existing));
        List<Long> couponIds = shopCoupons.stream().map(ShopCouponMapDTO::getCouponId)
                .collect(Collectors.toList());
        List<Coupon> dbCoupon = couponService.getBaseMapper().selectBatchIds(couponIds);
        if (CollectionUtils.isEmpty(dbCoupon)) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        //过滤
        shopCoupons = dbCoupon.stream().filter(coupon -> {
            // 只有未开始 或者进行中的优惠券才可以进行违规下架
            return coupon.notStated() || coupon.running();
        }).map(coupon -> {
            ShopCouponMapDTO dto = new ShopCouponMapDTO();
            dto.setShopId(coupon.getShopId());
            dto.setCouponId(coupon.getId());
            //违规下架原因
            dto.setViolationReason(map.get(dto.getCouponId()).getViolationReason());
            return dto;
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(shopCoupons)) {
            throw CouponErrorEnum.COUPON_STATE_ERROR.exception();
        }
        this.platformCouponEdit(
                shopCoupons,
                (shopId, shopCouponList) -> {
                    for (ShopCouponMapDTO shopCouponMapDTO : shopCouponList) {
                        boolean update = couponService.lambdaUpdate()
                                //平台下架
                                .set(Coupon::getStatus, CouponStatus.BANED)
                                //违规原因
                                .set(Coupon::getViolationReason, shopCouponMapDTO.getViolationReason())
                                .eq(Coupon::getId, shopCouponMapDTO.getCouponId())
                                .update();
                        if (!update) {
                            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED);
                        }
                    }
                }
        );

    }

    @Override
    public Coupon coupon(Long shopId, Long couponId) {
        return couponPlusService.getCoupon(shopId, couponId)
                .peek(
                        coupon -> {
                            if (!coupon.getProductType().getIsAssigned()) {
                                return;
                            }
                            coupon.setProductIds(couponPlusService.getProductIds(shopId, couponId));
                        }
                ).getOrNull();
    }

    /**
     * 店铺端下架优惠券 只有处于【进行中】的活动才能执行【下架】操作
     *
     * @param shopCouponMapDTO 优惠券信息
     * @return 下架成功 true  false
     */
    @Override
    public Boolean shopCouponOffShelf(ShopCouponMapDTO shopCouponMapDTO) {
        Coupon dbCoupon = couponService.getBaseMapper().selectById(shopCouponMapDTO.getCouponId());
        if (Objects.isNull(dbCoupon)) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (!ISystem.shopIdMust().equals(dbCoupon.getShopId())) {
            //店铺id不匹配
            throw CouponErrorEnum.SHOP_NOT_MATCH.exception();
        }
        if (!dbCoupon.running()) {
            //不是运行中的优惠券活动
            throw CouponErrorEnum.COUPON_STATE_ERROR.exception();
        }
        RedisUtil.doubleDeletion(
                () -> {
                    boolean update = couponService.lambdaUpdate()
                            // 优惠券下架
                            .set(Coupon::getStatus, CouponStatus.SHOP_BANED)
                            .eq(Coupon::getId, shopCouponMapDTO.getCouponId())
                            .update();
                    if (!update) {
                        throw new GlobalException(SystemCode.DATA_UPDATE_FAILED);
                    }
                },
                () -> RedisUtil.delete(RedisUtil.key(CouponConstant.COUPON_CACHE_KEY,
                        dbCoupon.getShopId(), dbCoupon.getId()))
        );
        return Boolean.TRUE;
    }

    /**
     * 查询违规下架原因
     *
     * @param couponId 优惠券id
     * @return 违规下架原因
     */
    @Override
    public String queryIllegalReason(Long couponId) {
        Coupon coupon = couponService.getBaseMapper().selectById(couponId);
        if (Objects.isNull(coupon)) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }

        if (ClientType.SHOP_CONSOLE.equals(ISystem.clientTypeMust())) {
            // 属于店铺端查看
            if (!coupon.getShopId().equals(ISystem.shopIdMust())) {
                // 店铺id不匹配
                throw CouponErrorEnum.SHOP_NOT_MATCH.exception();
            }
        }
        if (!coupon.platformIllegalOffShelf()) {
            //不是违规下架的优惠券
            throw CouponErrorEnum.COUPON_STATE_ERROR.exception();
        }
        return coupon.getViolationReason();
    }

    @Override
    public List<Coupon> queryShopCouponForDecoration(Long shopId) {
        List<Coupon> list = couponService.lambdaQuery()
                .eq(Coupon::getShopId, shopId)
                .eq(Coupon::getStatus, CouponStatus.OK)
                .orderBy(Boolean.TRUE, Boolean.TRUE, Coupon::getType)
                .list(new Page<>(1, 2));
        if (Objects.isNull(list)) {
            return Collections.emptyList();
        }
        list = list.stream().map(coupon -> {
                    String consumerDesc = coupon.getType().getConsumerDesc(coupon.getRequiredAmount(),
                            coupon.getAmount(), coupon.getDiscount());
                    coupon.setConsumerDesc(consumerDesc);
                    return coupon;
                }
        ).collect(Collectors.toList());
        return list;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void giftsToUser(Long shopId, GiftsToUserDTO giftsToUser) {
        CouponDTO coupon = giftsToUser.getCoupon();
        coupon.validParam(giftsToUser.isPlatform());
        Coupon couponEntity = coupon.toCouponEntity(shopId);
        couponEntity.setId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(couponEntity).longValue());

        saveCouponUser(giftsToUser, coupon, couponEntity);
    }

    private void saveCouponUser(GiftsToUserDTO giftsToUser, CouponDTO coupon, Coupon couponEntity) {
        // 用户优惠券信息-用户信息处理
        Map<Long, CouponUserInfoDTO> userBaseInfoByCouponUserId = getCouponUserBaseInfo(giftsToUser.getUserIds(), giftsToUser.isPlatform());

        List<CouponUser> couponUsers = giftsToUser.getUserIds()
                .stream()
                .flatMap(
                        userId -> LongStream.range(0, coupon.getNum())
                                .boxed()
                                .map(index -> couponEntity.newCouponUser(userBaseInfoByCouponUserId.get(userId), couponEntity.calcStartAnEndDate(), null))
                ).collect(Collectors.toList());
        boolean success = couponUserService.saveBatch(couponUsers);
        if (!success) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED);
        }
    }


    private void platformCouponEdit(List<ShopCouponMapDTO> shopCoupons,
                                    BiConsumer<Long, List<ShopCouponMapDTO>> shopIdCouponIdsConsumer) {
        Map<Long, List<ShopCouponMapDTO>> shopIdCouponIdsMap = new HashMap<>(CommonPool.NUMBER_FIFTEEN);
        Set<String> couponCacheKeys = new HashSet<>(CommonPool.NUMBER_THIRTY);
        shopCoupons.forEach(
                shopCoupon -> {
                    Long shopId = shopCoupon.getShopId();
                    Long couponId = shopCoupon.getCouponId();
                    List<ShopCouponMapDTO> shopCouponMapDTOS = shopIdCouponIdsMap.computeIfAbsent(shopId,
                            (key) -> Lists.newArrayList());
                    shopCouponMapDTOS.add(shopCoupon);
                    //店铺优惠券的缓存key
                    couponCacheKeys.add(RedisUtil.key(CouponConstant.COUPON_CACHE_KEY, shopId, couponId));
                }
        );
        RedisUtil.doubleDeletion(
                () -> shopIdCouponIdsMap.forEach(shopIdCouponIdsConsumer),
                () -> RedisUtil.delete(couponCacheKeys)
        );
    }

    /**
     * 用户优惠券信息-用户信息处理
     *
     * @param couponUserIds 优惠券用户Id
     * @param isPlatform    是否是平台赠送
     * @return 下架成功 true  false
     */
    public Map<Long, CouponUserInfoDTO> getCouponUserBaseInfo(Set<Long> couponUserIds, boolean isPlatform) {
        // 领券方式
        CouponCollectionType collectType = isPlatform ? CouponCollectionType.PLATFORM_GIFT : CouponCollectionType.SHOP_GIFT;
        // 优惠券用户信息
        Map<Long, UserInfoVO> userInfoMap = uaaRpcService.getUserDataBatchByUserIds(couponUserIds);

        // 赠券用户
        SecureUser<Object> giftSecureUser = ISecurity.userMust();
        CouponUserInfoDTO.UserInfo giftUserBaseInfo = new CouponUserInfoDTO.UserInfo().setUserId(giftSecureUser.getId()).setMobile(giftSecureUser.getMobile()).setNickname(giftSecureUser.getNickname());

        // 拼凑优惠券用户信息, 并以优惠券用户Id为Key
        return couponUserIds.stream().map(couponUserId -> {
            CouponUserInfoDTO tempUser = new CouponUserInfoDTO();
            tempUser.setGiftUserInfo(giftUserBaseInfo)
                    .setCouponUserId(couponUserId)
                    .setCollectType(collectType);
            UserInfoVO couponUserInfoVo = userInfoMap.get(couponUserId);
            if (null != couponUserInfoVo) {
                tempUser.setCouponUserInfo(new CouponUserInfoDTO.UserInfo().setUserId(couponUserInfoVo.getUserId()).setMobile(couponUserInfoVo.getMobile()).setNickname(couponUserInfoVo.getNickname()));
            }
            return tempUser;
        }).collect(Collectors.toMap(CouponUserInfoDTO::getCouponUserId, Function.identity()));

    }

}
