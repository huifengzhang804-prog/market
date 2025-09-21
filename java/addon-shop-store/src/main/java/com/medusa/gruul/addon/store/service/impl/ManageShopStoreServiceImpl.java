package com.medusa.gruul.addon.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.medusa.gruul.addon.store.model.dto.ShopStoreDTO;
import com.medusa.gruul.addon.store.model.dto.ShopStoreOperateDTO;
import com.medusa.gruul.addon.store.model.enums.StoreStatus;
import com.medusa.gruul.addon.store.model.param.ShopStoreParam;
import com.medusa.gruul.addon.store.model.vo.ShopStoreListVO;
import com.medusa.gruul.addon.store.mp.entity.ShopStore;
import com.medusa.gruul.addon.store.mp.entity.ShopStoreOrder;
import com.medusa.gruul.addon.store.mp.service.IShopStoreOrderService;
import com.medusa.gruul.addon.store.mp.service.IShopStoreService;
import com.medusa.gruul.addon.store.service.ManageShopStoreService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xiaoq
 * @Description 店铺门店管理实现
 * @date 2023-03-07 17:31
 */
@Service
@RequiredArgsConstructor
public class ManageShopStoreServiceImpl implements ManageShopStoreService {

	private final IShopStoreService shopStoreService;

	private final ShopRpcService shopRpcService;

	private final IShopStoreOrderService shopStoreOrderService;

	/**
	 * 店铺门店新增
	 *
	 * @param shopStoreDTO 店铺门店DTO
	 */
	@Override
	public void issueShopStore(ShopStoreDTO shopStoreDTO, Long shopId) {
		Optional<ShopStore> existingStore = Optional.ofNullable(
				shopStoreService.lambdaQuery()
						.eq(ShopStore::getFunctionaryPhone, shopStoreDTO.getFunctionaryPhone())
						.one()
		);
		existingStore.ifPresent(store -> {
			throw new GlobalException("当前手机号已是店员");
		});

		long storeCount = shopStoreService.lambdaQuery()
				.eq(ShopStore::getShopId, shopId)
				.count();
		if (storeCount >= CommonPool.NUMBER_FIVE) {
			throw new GlobalException("门店保存失败,每个店铺只能存在5个门店");
		}

		ShopStore shopStore = shopStoreDTO.coverShopStore();
		shopStore.setStatus(StoreStatus.NORMAL)
				.setShopId(shopId)
				.setShopName(shopRpcService.getShopInfoByShopId(shopId).getName());
		if (!shopStoreService.save(shopStore)) {
			throw new GlobalException("店铺门店保存失败");
		}
	}

	/**
	 * 店铺门店删除
	 *
	 * @param shopId 店铺id
	 * @param id     店铺门店id
	 */
	@Override
	public void deletedShopStore(Long shopId, Long id) {
		List<ShopStoreOrder> shopStoreOrders = shopStoreOrderService.lambdaQuery()
				.eq(ShopStoreOrder::getShopStoreId, id)
				.in(ShopStoreOrder::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER, PackageStatus.WAITING_FOR_RECEIVE).list();
		if (CollUtil.isNotEmpty(shopStoreOrders)) {
			throw new GlobalException("当前门店存在未结余订单。不可删除");
		}
		boolean remove = shopStoreService.lambdaUpdate().eq(ShopStore::getShopId, shopId).eq(BaseEntity::getId, id).remove();
		if (!remove) {
			throw new GlobalException("门店删除失败");
		}
	}

	/**
	 * 店铺门店修改
	 *
	 * @param shopStoreDTO 店铺门店DTO
	 */
	@Override
	public void updateShopStore(ShopStoreDTO shopStoreDTO) {
		ShopStore shopStore = shopStoreDTO.coverShopStore();
		shopStore.setId(shopStoreDTO.getId());
		shopStore.setShopId(ISecurity.userMust().getShopId());
		if (!shopStoreService.updateById(shopStore)) {
			throw new GlobalException("店铺信息修改失败");
		}
	}

	/**
	 * 查询店铺门店列表
	 *
	 * @param shopStoreParam 查询param
	 * @return IPage<店铺门店列表>
	 */
	@Override
	public IPage<ShopStoreListVO> getShopStoreListVO(ShopStoreParam shopStoreParam) {
		ISecurity.match()
				.ifAnySuperAdmin(secureUser -> shopStoreParam.setShopId(0L))
				.other(secureUser -> shopStoreParam.setShopId(secureUser.getShopId()));
		return shopStoreService.getShopStoreListVO(shopStoreParam);
	}

	/**
	 * 修改店铺门店状态
	 *
	 * @param shopStoreOperate 店铺门店操作DTO
	 * @param status           修改状态
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateShopStoreStatus(List<ShopStoreOperateDTO> shopStoreOperate, StoreStatus status) {
        Set<Long> shopIds = shopStoreOperate.stream()
                .map(ShopStoreOperateDTO::getShopId)
                .collect(Collectors.toSet());

		if (StoreStatus.SHOP_FORBIDDEN.equals(status)) {
			Set<Long> allIds = shopStoreOperate.stream()
					// Flatten the Set<Long> from each DTO
					.flatMap(dto -> dto.getIds().stream())
					.collect(Collectors.toSet());
			//禁用店铺需要判断存在是否待核销的订单
			boolean hasWaitingOrders =  shopStoreOrderService.lambdaQuery()
					.eq(ShopStoreOrder::getPackageStatus,
							Lists.newArrayList( PackageStatus.WAITING_FOR_DELIVER,PackageStatus.WAITING_FOR_RECEIVE))
					.in(ShopStoreOrder::getShopStoreId,allIds)
					.in(ShopStoreOrder::getShopId, shopIds)
					.exists();
			if (hasWaitingOrders) {
				throw new GlobalException("有待核销订单,无法禁用");
			}

		}

        ISecurity.match()
				.ifAnySuperAdmin(
						secureUser -> shopStoreOperate.forEach(
								bean -> {
									Boolean disable = TenantShop.disable(() -> shopStoreService.lambdaUpdate()
											.set(ShopStore::getStatus, StoreStatus.PLATFORM_FORBIDDEN)
											.eq(ShopStore::getShopId, bean.getShopId())
											.in(BaseEntity::getId, bean.getIds())
											.update());
									if (!disable) {
										throw new GlobalException("平台修改店铺门店状态失败,请稍后再试");
									}
								}
						)
				)
				.other(secureUser -> {
					Set<Long> storeIds = shopStoreOperate.get(0).getIds();
					// 过滤掉平台下架门店
					List<Long> validStoreIds = shopStoreService.lambdaQuery()
							.select(BaseEntity::getId)
							.in(BaseEntity::getId, storeIds)
							.ne(ShopStore::getStatus, StoreStatus.PLATFORM_FORBIDDEN)
							.list()
							.stream()
							.map(BaseEntity::getId)
							.collect(Collectors.toList());
					if (!validStoreIds.isEmpty()) {
						boolean update = shopStoreService.lambdaUpdate()
								.set(ShopStore::getStatus, status)
								.in(BaseEntity::getId, validStoreIds)
								.update();
						if (!update) {
							throw new GlobalException("店铺修改店铺门店状态失败,请稍后再试");
						}
					}
				});
	}

	/**
	 * 修改店铺名称
	 *
	 * @param shopInfo 店铺信息
	 */
	@Override
	public void updateShopName(ShopInfoVO shopInfo) {
		shopStoreService.lambdaUpdate()
				.set(ShopStore::getShopName, shopInfo.getName())
				.eq(ShopStore::getShopId, shopInfo.getId())
				.update();
	}
}
