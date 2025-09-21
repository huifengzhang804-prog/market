package com.medusa.gruul.common.sharding.jdbc;

/**
 * @author 张治保
 * @since 2024/5/9
 */
public interface ShardingHelper {

    /**
     * 获取分库分表工具
     *
     * @param db    数据库数量 如 4
     * @param table 表数量 如 8
     * @return DbTableNum
     */
    static DbTableNum dbTableNum(int db, int table) {
        return new DbTableNum(db, table);
    }

    record DbTableNum(int db, int table) {

        public int db(long shardingId) {
            return (int) shardingId % db;
        }

        public int table(long shardingId) {
            return (int) shardingId / db % table;
        }


    }


}
