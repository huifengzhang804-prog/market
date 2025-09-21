package com.medusa.gruul.afs.service.util;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.afs.api.enums.AfsType;
import com.medusa.gruul.afs.service.model.enums.AfsError;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.ShopOrder;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.enums.PackageStatus;
import io.vavr.control.Option;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/4
 */
public interface AfsUtil {
    String AFS_NO_TEMPLATE = "{}-{}{}";

    /**
     * 生成售后编号
     *
     * @param orderNo 订单编号
     * @param afsType 售后类型
     * @param number  编号
     * @return 售后编号
     */
    static String no(String orderNo, AfsType afsType, int number) {
        return StrUtil.format(
                AFS_NO_TEMPLATE, orderNo, afsType.getNoPrefix(), String.format("%02d", number)
        );
    }

    /**
     * 售后 检查店铺订单状态
     *
     * @param buyerId       买家id
     * @param shopOrderItem 店铺订单商品详情
     */
    static void checkShopOrderItemForAfs(Long buyerId, ShopOrderItem shopOrderItem) {
        ItemStatus status = shopOrderItem.getStatus();
        AfsError.ITEM_CLOSED.falseThrow(ItemStatus.OK == status);
        PackageStatus packageStatus = shopOrderItem.getPackageStatus();
        if (!packageStatus.isCanAfs()) {
            throw AfsError.ITEM_CANNOT_AFS_STATUS.dataEx(packageStatus);
        }
        ShopOrder shopOrder = Option.of(shopOrderItem.getShopOrder()).getOrElseThrow(AfsError.ITEM_NOT_EXIST::exception);
        AfsError.ITEM_CLOSED.trueThrow(shopOrder.getStatus().isClosed());
        Order order = Option.of(shopOrder.getOrder()).getOrElseThrow(AfsError.ITEM_NOT_EXIST::exception);
        AfsError.ITEM_CLOSED.trueThrow(order.getStatus().isClosed());
        SecureCodes.PERMISSION_DENIED.falseThrow(buyerId.equals(order.getBuyerId()));
        AfsError.ITEM_CANNOT_AFS_STATUS.trueThrow(OrderStatus.UNPAID == order.getStatus());
        // 判断 是否是同城的订单 同城订单 售后有额外限制
        if (DistributionMode.INTRA_CITY_DISTRIBUTION == order.getDistributionMode()) {
            //同城订单 只能在 未发货 或 已送达之后才能申请售后
            if (PackageStatus.WAITING_FOR_DELIVER == shopOrderItem.getPackageStatus() || BooleanUtil.isTrue(shopOrder.getExtra().getIcReceivable())) {
                return;
            }
            throw AfsError.ITEM_CANNOT_AFS_STATUS.exception();
        }

    }

}
