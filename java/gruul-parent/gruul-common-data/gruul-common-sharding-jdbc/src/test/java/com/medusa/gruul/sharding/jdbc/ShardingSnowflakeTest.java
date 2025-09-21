package com.medusa.gruul.sharding.jdbc;

import cn.hutool.core.util.IdUtil;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * 测试雪花算法生成的 id 分库分表结果 数据分布均匀情况
 * todo 雪花算法在低并发下 每毫秒自赠序列 sequence 都是从0开始（偶数） 导致分库分表不均匀 发生数据倾斜
 * todo 解决方式：库与表的数量设置为奇数 且 表数量不能是库数量的整数倍  否则会导致数据倾斜
 *
 * @author 张治保
 * @since 2024/5/16
 */
public class ShardingSnowflakeTest {

    //雪花算法生成的店铺 id
    private final Set<Long> shopIds = new HashSet<>();
    private final Random random = new Random();

    {
        for (int i = 0; i < 1000; i++) {
            shopIds.add(IdUtil.getSnowflakeNextId());
            int i1 = random.nextInt(100);
            //百分之五的概率发生出现高并发 生成连续的 id
            if (i1 < 5) {
                continue;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * 三库 五表
     */
    @Test
    void test35() {
        group(3, 5);
    }

    /**
     * 五库 七表
     */
    @Test
    void test57() {
        group(5, 7);
    }

    /**
     * 7库 9表
     */
    @Test
    void test79() {
        group(7, 9);
    }


    /**
     * 分库分表 均匀性测试
     *
     * @param db 分库数
     * @param tb 分表数
     */
    void group(int db, int tb) {
        Map<Integer, Map<Integer, Integer>> result = new HashMap<>();
        for (Long shopId : shopIds) {
            int curDb = (int) (shopId % db);
            Map<Integer, Integer> tables = result.computeIfAbsent(curDb, key -> new HashMap<>());

            int curTable = (int) (shopId % tb);
            Integer table = tables.computeIfAbsent(curTable, key -> 0);

            tables.put(curTable, table + 1);
        }
        for (int dbIndex = 0; dbIndex < db; dbIndex++) {
            System.out.println("db_" + dbIndex);
            Map<Integer, Integer> tables = result.getOrDefault(dbIndex, new HashMap<>());

            for (int tbIndex = 0; tbIndex < tb; tbIndex++) {
                Integer table = tables.getOrDefault(tbIndex, 0);
                System.out.println("    tb_" + tbIndex + "-->" + table);

            }
        }
    }
}
