package com.medusa.gruul.common.security.model.enums;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.common.security.model.constant.SecureConst;
import com.medusa.gruul.common.system.model.model.ClientType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/2/23
 */
@Getter
@RequiredArgsConstructor
public enum Roles {


    /**
     * 开发者
     */
    DEVELOPER(1, 1L, "开发者", false, ClientType.DEVELOPER_CONSOLE, 0, Set.of()),

    /**
     * 超级管理员 最大权限
     */
    SUPER_ADMIN(2, 2L, "超级管理员", false, ClientType.PLATFORM_CONSOLE, 0, SecureConst.ALL_DESTINATION_TEMPLATE),
    /**
     * 普通用户 （消费者）
     */
    USER(3, 3L, "用户", false, ClientType.CONSUMER, 0, SecureConst.ALL_DESTINATION_TEMPLATE),
    /**
     * 禁止评论 用户
     */
    FORBIDDEN_COMMENT(4, 4L, "禁止评论", false, ClientType.CONSUMER, 1, Set.of()),
    /**
     * 禁止下单用户
     */
    FORBIDDEN_ORDER(5, 5L, "禁止下单", false, ClientType.CONSUMER, 1, Set.of()),
    /**
     * 黑名单
     */
    BLACK_LIST(6, 6L, "黑名单", false, ClientType.CONSUMER, 1, Set.of()),

    /**
     * 自定义超级管理员
     */
    SUPER_CUSTOM_ADMIN(7, null, "子超级管理员", true, ClientType.PLATFORM_CONSOLE, 1, SecureConst.ALL_DESTINATION_TEMPLATE),

    /**
     * 管理员 当前商铺的最大权限
     */
    ADMIN(8, null, "管理员", false, ClientType.SHOP_CONSOLE, 0, SecureConst.ALL_DESTINATION_TEMPLATE),

    /**
     * 自定义管理员 管理员自定义权限管理员
     */
    CUSTOM_ADMIN(9, null, "子管理员", true, ClientType.SHOP_CONSOLE, 1, SecureConst.ALL_DESTINATION_TEMPLATE),

    /**
     * 店铺门店
     */
    SHOP_STORE(10, null, "店铺门店", false, ClientType.STORE, 0, SecureConst.ALL_DESTINATION_TEMPLATE),

    /**
     * 主播
     */
    ANCHOR(11, null, "主播", false, ClientType.CONSUMER, 1, Set.of()),

    /**
     * 供应商管理员
     */
    SUPPLIER_ADMIN(12, null, "供应商管理员", false, ClientType.SUPPLIER_CONSOLE, 0, SecureConst.ALL_DESTINATION_TEMPLATE),

    /**
     * 供应商子管理员
     */
    SUPPLIER_CUSTOM_ADMIN(13, null, "供应商子管理员", false, ClientType.SUPPLIER_CONSOLE, 1, SecureConst.ALL_DESTINATION_TEMPLATE),

    ;

    /**
     * 数据库 值
     */
    @EnumValue
    private final Integer value;

    /**
     * 对应的角色id null为动态角色
     */
    private final Long roleId;

    /**
     * 名称描述
     */
    private final String desc;

    /**
     * 是否需要权限
     */
    private final boolean permNeed;

    /**
     * 客户端类型
     */
    private final ClientType clientType;

    /**
     * 用于判断是否是主要角色 值最小的是主要角色
     * 当一个用户拥有多个角色时 以主要角色为准 其他角色为子角色
     */
    private final int mainSort;

    /**
     * websocket destination
     */
    private final Set<String> destination;

    /**
     * 主要角色排序比较器 用于判断是否是主要角色 值最小的是主要角色
     *
     * @param clientType 客户端类型
     * @return 比较器
     */
    public static Comparator<Roles> mainSortComparator(ClientType clientType) {
        return Comparator.comparing((Roles roles) -> roles.getClientType() != clientType)
                .thenComparing(Roles::getMainSort);
    }

    /**
     * 获取角色对应的websocket destination
     *
     * @param shopId 店铺id
     * @param userId 用户id
     * @return destinations 集合
     */
    public Set<String> destinations(Long shopId, Long userId) {
        Set<String> destinations = getDestination();
        if (CollUtil.isEmpty(destinations)) {
            return Set.of();
        }
        return destinations.stream()
                .map(destination -> SecureConst.destination(destination, this.getClientType(), shopId, userId))
                .collect(Collectors.toSet());
    }

}
