package com.medusa.gruul.global.model.constant;


/**
 * 定义 切面执行顺序
 * 同一切点  越小越先执行 越大越后执行
 *
 * @author 张治保
 * date 2022/3/10
 */
public interface AspectOrder {

    /* 从小到大   */

    /**
     * 日志打印切面
     */
    int LOG_ASPECT = Integer.MIN_VALUE;

    /**
     * idem 幂等校验 切面
     */
    int IDEM_ASPECT = AspectOrder.LOG_ASPECT + 100;


    /* 从大到小   */
    /**
     * transaction @Transactional
     */
    int TRANSACTIONAL_ASPECT = Integer.MAX_VALUE;
    
    /**
     * 数据源切换切面
     */
    int DATASOURCE_ASPECT = AspectOrder.TRANSACTIONAL_ASPECT - 100;

    /**
     * cache 缓存 @Cacheable @CacheEvict ..
     */
    int CACHE_ASPECT = AspectOrder.DATASOURCE_ASPECT - 100;

    /**
     * Redisson分布式锁 切面
     */
    int REDISSON_ASPECT = AspectOrder.CACHE_ASPECT - 100;


}
