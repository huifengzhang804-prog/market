package com.medusa.gruul.goods.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.goods.api.entity.SupplierRateRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 供应商评分记录数据层
 *
 * @author xiaoq
 * @Description SupplierRateRecordMapper.java
 * @date 2022-12-07 13:38
 */
public interface SupplierRateRecordMapper extends BaseMapper<SupplierRateRecord> {
    /**
     * 获取供应商真实评分
     *
     * @param supperIdList 供应商id
     * @return List<SupplierRateRecord>
     */
    List<SupplierRateRecord> querySupplierRealRate(@Param("supperIdList") Set<Long> supperIdList);
}
