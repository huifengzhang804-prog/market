package com.medusa.gruul.afs.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.afs.service.model.bo.AfsOrderQueryBO;
import com.medusa.gruul.afs.service.model.dto.AfsPageDTO;
import com.medusa.gruul.afs.service.model.dto.AfsQueryDTO;
import com.medusa.gruul.afs.service.mp.entity.AfsHistory;
import com.medusa.gruul.afs.service.mp.entity.AfsOrder;
import com.medusa.gruul.afs.service.mp.entity.AfsOrderItem;
import com.medusa.gruul.afs.service.mp.service.IAfsHistoryService;
import com.medusa.gruul.afs.service.mp.service.IAfsOrderItemService;
import com.medusa.gruul.afs.service.mp.service.IAfsOrderService;
import com.medusa.gruul.afs.service.service.AfsQueryService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/9
 */
@Service
@RequiredArgsConstructor
public class AfsQueryServiceImpl implements AfsQueryService {

    private final IAfsOrderService afsOrderService;
    private final IAfsOrderItemService afsOrderItemService;
    private final IAfsHistoryService afsHistoryService;

    private final UaaRpcService uaaRpcService;

    @Override
    public IPage<AfsOrder> afsOrderPage(AfsPageDTO afsPage) {
        IPage<AfsOrder> afsOrderIPage = TenantShop.disable(() -> afsOrderService.afsOrderPage(afsPage));
        //售后状态文案
        List<AfsOrder> records = afsOrderIPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return afsOrderIPage;
        }
        records.forEach(afsOrder -> {
            AfsStatus status = afsOrder.getStatus();
            afsOrder.setStatusContent(status.getStatusStr() + " " + status.getStatusDesc());
        });
        Set<Long> buyerIds = records.stream().map(AfsOrder::getBuyerId).collect(Collectors.toSet());
        Map<Long, UserInfoVO> userInfoVOMap = uaaRpcService.getUserDataBatchByUserIds(
                buyerIds);
        records.forEach(afsOrder -> {
            Long buyerId = afsOrder.getBuyerId();
            UserInfoVO userInfoVO = userInfoVOMap.get(buyerId);
            if (userInfoVO != null) {
                afsOrder.setBuyerPhone(userInfoVO.getMobile());
            }
        });
        return afsOrderIPage;
    }

    @Override
    public Option<AfsOrder> getCurrentAfsOrderDetail(String afsNo) {
        AfsOrderQueryBO query = new AfsOrderQueryBO().setAfsNo(afsNo);
        ISecurity.match()
                .ifAnySupplierAdmin(secureUser -> query.setSupplierId(secureUser.getShopId()))
                .ifAnyShopAdmin(secureUser -> query.setShopId(secureUser.getShopId()))
                .ifUser(secureUser -> query.setBuyerId(secureUser.getId()));
        List<AfsOrder> afsOrders = TenantShop.disable(() -> afsOrderService.getAfsOrderDetail(query));
        return Option.when(CollUtil.isNotEmpty(afsOrders), () -> afsOrders.get(CommonPool.NUMBER_ZERO));
    }

    @Override
    public List<AfsOrder> getAfsHistory(String afsNo) {
        List<AfsOrder> afsOrderList = this.getCurrentAfsOrderDetail(afsNo)
                .map(
                        order -> {
                            AfsOrderQueryBO query = new AfsOrderQueryBO().setHistory(Boolean.TRUE)
                                    .setAfsNo(afsNo)
                                    .setProductId(order.getAfsOrderItem().getProductId())
                                    .setItemId(order.getShopOrderItemId());
                            ISecurity.match()
                                    .ifAnySupplierAdmin(secureUser -> query.setSupplierId(secureUser.getShopId()))
                                    .ifAnyShopAdmin(secureUser -> query.setShopId(secureUser.getShopId()))
                                    .ifUser(secureUser -> query.setBuyerId(secureUser.getId()));

                            return TenantShop.disable(() -> afsOrderService.getAfsOrderDetail(query));
                        }
                )
                .getOrElseThrow(SystemCode.DATA_NOT_EXIST::exception);
        if (CollUtil.isNotEmpty(afsOrderList)) {
            AfsOrder afsOrder = afsOrderList.get(CommonPool.NUMBER_ZERO);
            uaaRpcService.getUserDataByUserId(afsOrder.getBuyerId())
                    .peek(
                            user -> afsOrder.setBuyerPhone(user.getMobile())
                                    .setBuyerAvatar(user.getAvatar())
                    );
            if (AfsStatus.REFUND_REQUEST.equals(afsOrder.getStatus()) ||
                    AfsStatus.RETURN_REFUND_REQUEST.equals(afsOrder.getStatus())) {
                //最新的一条售后历史为退款申请 或者退货退款申请 则把自动通过的截止时间标上
                afsOrder.setOverdueTime(afsOrder.getCreateTime().plusSeconds(afsOrder.getKeyNodeTimeout().getRequestAgreeTimeout()));
            }
        }
        return afsOrderList;
    }

    @Override
    public List<AfsOrderItem> afsOrderItems(String afsNo) {
        return Option.of(TenantShop.disable(() -> afsOrderService.lambdaQuery().eq(AfsOrder::getNo, afsNo).one()))
                .map(afsOrder -> afsOrderItemService.lambdaQuery().eq(AfsOrderItem::getAfsNo, afsNo).list())
                .getOrElseThrow(SystemCode.DATA_NOT_EXIST::exception);
    }

    @Override
    public Map<String, AfsHistory> lastAfsHistory(Set<String> afsNos) {

        return afsHistoryService.lastAfsHistory(afsNos);

    }

    @Override
    public Integer staticsStatusCount(AfsQueryDTO afsQuery) {
//        TenantShop.disable(() -> afsOrderService.afsOrderPage(afsPage));
        Integer count = TenantShop.disable(() -> afsOrderService.staticsStatusCount(afsQuery));
        return count;
    }


}
