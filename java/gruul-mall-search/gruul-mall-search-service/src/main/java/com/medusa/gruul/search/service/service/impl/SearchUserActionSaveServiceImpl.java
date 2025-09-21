package com.medusa.gruul.search.service.service.impl;

import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.search.api.enums.OperationType;
import com.medusa.gruul.search.service.mp.entity.SearchUserAction;
import com.medusa.gruul.search.service.mp.service.ISearchUserActionService;
import com.medusa.gruul.search.service.service.SearchUserActionSaveService;
import com.medusa.gruul.shop.api.model.dto.ShopOperationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 检索用户行为SaveServiceImpl
 *
 * @author xiaoq
 * @Description SearchUserActionSaveServiceImpl.java
 * @date 2023-12-08 14:56
 */
@Service
@RequiredArgsConstructor
public class SearchUserActionSaveServiceImpl implements SearchUserActionSaveService {
    private final ISearchUserActionService searchUserActionService;

    @Override
    public void saveSearchShop(ShopOperationDTO shopOperation) {
        List<SearchUserAction> actions = new ArrayList<>();
        List<SearchUserAction> searchUserActions = new ArrayList<>();

        shopOperation.getShopIds().forEach(shopId -> {
            // 将当前shopId的操作添加到现有列表
            searchUserActions.addAll(actionsToSave(shopOperation.getUserId(), shopId, actions, OperationType.SEARCH_SHOP));
        });

        if (!searchUserActions.isEmpty()) {
            // 只有在有操作需要保存时才保存
            TenantShop.disable(() ->
                    searchUserActionService.saveOrUpdateBatch(searchUserActions));
        }
    }

    @Override
    public void savePayShop(OrderPaidBroadcastDTO orderPaidBroadcast) {
        List<SearchUserAction> actions = new ArrayList<>();
        List<SearchUserAction> searchUserActions = new ArrayList<>();
        Long buyerId = orderPaidBroadcast.getBuyerId();
        orderPaidBroadcast.getShopPayAmounts().forEach(
                shopPayAmountDTO -> searchUserActions.addAll(
                        actionsToSave(buyerId, shopPayAmountDTO.getShopId(), actions, OperationType.CONSUMPTION_SHOP)
                )
        );

        if (!searchUserActions.isEmpty()) {
            // 只有在有操作需要保存时才保存
            TenantShop.disable(() ->
                    searchUserActionService.saveOrUpdateBatch(searchUserActions));
        }
    }

    @Override
    public void saveUserAttentionShop(ShopOperationDTO shopOperation) {
        List<SearchUserAction> actions = new ArrayList<>();
        List<SearchUserAction> searchUserActions = new ArrayList<>();

        shopOperation.getShopIds().forEach(shopId -> {
            // 将当前shopId的操作添加到现有列表
            searchUserActions.addAll(actionsToSave(shopOperation.getUserId(), shopId, actions, OperationType.ATTENTTION_SHOP));
        });

        if (!searchUserActions.isEmpty()) {
            // 只有在有操作需要保存时才保存
            TenantShop.disable(() ->
                    searchUserActionService.saveOrUpdateBatch(searchUserActions));
        }
    }

    @Override
    public void userFootMarkAdd(Long shopId, Long userId) {
        List<SearchUserAction> actions = new ArrayList<>();
        List<SearchUserAction> searchUserActions = new ArrayList<>(actionsToSave(userId, shopId, actions, OperationType.STROLL_SHOP));

        if (!searchUserActions.isEmpty()) {
            // 只有在有操作需要保存时才保存
            TenantShop.disable(() ->
                    searchUserActionService.saveOrUpdateBatch(searchUserActions));
        }
    }

    private List<SearchUserAction> actionsToSave(Long userId, Long shopId, List<SearchUserAction> actions, OperationType operationType) {
        SearchUserAction searchUserAction = TenantShop.disable(() ->
                searchUserActionService.lambdaQuery()
                        .eq(SearchUserAction::getUserId, userId)
                        .eq(SearchUserAction::getShopId, shopId)
                        .one()
        );


        if (searchUserAction == null) {
            // 新增
            SearchUserAction newUserAction = new SearchUserAction();
            newUserAction.setUserId(userId);
            newUserAction.setShopId(shopId);
            newUserAction.setOperation(Collections.singletonList(operationType));
            actions.add(newUserAction);
        } else if (!searchUserAction.getOperation().contains(operationType)) {
            // 如果Operation不包含SEARCH_SHOP，进行修改
            List<OperationType> operation = new ArrayList<>(searchUserAction.getOperation());
            operation.add(operationType);
            searchUserAction.setOperation(operation);
            // 判断是否已经添加，避免重复添加
            if (!actions.contains(searchUserAction)) {
                actions.add(searchUserAction);
            }
        }
        return actions;
    }


}
