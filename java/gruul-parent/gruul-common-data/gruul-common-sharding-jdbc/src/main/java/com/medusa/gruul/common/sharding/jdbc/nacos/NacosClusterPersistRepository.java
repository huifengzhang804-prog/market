package com.medusa.gruul.common.sharding.jdbc.nacos;

import org.apache.shardingsphere.mode.repository.cluster.ClusterPersistRepository;
import org.apache.shardingsphere.mode.repository.cluster.ClusterPersistRepositoryConfiguration;
import org.apache.shardingsphere.mode.repository.cluster.LeaderExecutionCallback;
import org.apache.shardingsphere.mode.repository.cluster.listener.DataChangedEventListener;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * todo 暂未实现, 需要请自行实现
 *
 * @author 张治保
 * @since 2024/5/13
 */
public class NacosClusterPersistRepository implements ClusterPersistRepository {
    @Override
    public void init(ClusterPersistRepositoryConfiguration clusterPersistRepositoryConfiguration) {

    }

    @Override
    public long getRegistryCenterTime(String s) {
        return 0;
    }

    @Override
    public Object getRawClient() {
        return null;
    }

    @Override
    public int getNumChildren(String s) {
        return 0;
    }

    @Override
    public void addCacheData(String s) {

    }

    @Override
    public void evictCacheData(String s) {

    }

    @Override
    public Object getRawCache(String s) {
        return null;
    }

    @Override
    public void executeInLeader(String s, LeaderExecutionCallback leaderExecutionCallback) {

    }

    @Override
    public void updateInTransaction(String s, String s1) {

    }

    @Override
    public void persistEphemeral(String s, String s1) {

    }

    @Override
    public void persistExclusiveEphemeral(String s, String s1) {

    }

    @Override
    public boolean tryLock(String s, long l) {
        return false;
    }

    @Override
    public void unlock(String s) {

    }

    @Override
    public void watch(String s, DataChangedEventListener dataChangedEventListener, Executor executor) {

    }

    @Override
    public String get(String s) {
        return null;
    }

    @Override
    public String getDirectly(String s) {
        return null;
    }

    @Override
    public List<String> getChildrenKeys(String s) {
        return null;
    }

    @Override
    public boolean isExisted(String s) {
        return false;
    }

    @Override
    public void persist(String s, String s1) {

    }

    @Override
    public void update(String s, String s1) {

    }

    @Override
    public void delete(String s) {

    }

    @Override
    public void close() {

    }
}
