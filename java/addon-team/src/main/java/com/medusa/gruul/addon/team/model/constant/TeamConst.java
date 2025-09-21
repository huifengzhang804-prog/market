package com.medusa.gruul.addon.team.model.constant;

/**
 * @author 张治保
 * date 2023/3/6
 */
public interface TeamConst {

    /**
     * 团号前缀
     */
    String NO_PREFIX = "TM";

    /**
     * 团号 key
     */
    String NO_KEY = "addon:team:no";

    /**
     * 拼团活动开启 锁 key
     */
    String TEAM_ACTIVITY_OPEN_LOCK_KEY = "addon:team:activity:open:lock";

    /**
     * 团号锁 key
     */
    String TEAM_LOCK_KEY = "addon:team:lock";

    /**
     * 团号字段名
     */
    String NO_FIELD = "teamNo";

    /**
     * 拼团订单缓存 key 未支付是缓存订单信息
     */
    String ORDER_TEAM_CACHE_KEY = "addon:team:order";


    /**
     * 选择的拼团人数字段名
     */
    String SELECTED_USERS_FIELD = "userNum";

    /**
     * 商品拼团信息缓存 当活动开始时才缓存数据
     */
    String PRODUCT_TEAM_INFO_CACHE_KEY = "addon:team:product";

    /**
     * 用户拼团成功缓存
     */
    String USER_TEAM_SUCCESS_CACHE_KEY = "addon:team:success";

    /**
     * 拼团定时任务handler名称
     */
    String TEAM_OPEN_JOB_HANDLER = "teamOpenJobHandler";

    /**
     * 拼团定时任务handler名称
     */
    String TEAM_CLOSE_JOB_HANDLER = "teamCloseJobHandler";

    /**
     * 拼团订单定时任务handler名称
     */
    String TEAM_ORDER_CLOSE_JOB_HANDLER = "teamOrderCloseJobHandler";

}
