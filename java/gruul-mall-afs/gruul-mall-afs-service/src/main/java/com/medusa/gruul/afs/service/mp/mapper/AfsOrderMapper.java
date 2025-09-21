package com.medusa.gruul.afs.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.afs.service.model.bo.AfsOrderQueryBO;
import com.medusa.gruul.afs.service.model.dto.AfsPageDTO;
import com.medusa.gruul.afs.service.model.dto.AfsQueryDTO;
import com.medusa.gruul.afs.service.mp.entity.AfsOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 售后工单表 Mapper 接口
 * </p>
 *
 * @author 张治保
 * @since 2022-08-03
 */
public interface AfsOrderMapper extends BaseMapper<AfsOrder> {

    /**
     * 分页查询售后工单
     *
     * @param afsPage 分页查询参数
     * @return 分页查询结果
     */
    IPage<AfsOrder> afsOrderPage(@Param("query") AfsPageDTO afsPage);


    /**
     * 根绝查询条件 查询售后详情
     *
     * @param afsOrderQuery 查询条件
     * @return 查询结果
     */
    List<AfsOrder> getAfsOrderDetail(@Param("query") AfsOrderQueryBO afsOrderQuery);


    Integer staticsStatusCount(@Param("query") AfsQueryDTO afsQuery);
}
