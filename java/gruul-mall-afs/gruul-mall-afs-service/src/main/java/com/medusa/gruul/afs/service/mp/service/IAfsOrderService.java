package com.medusa.gruul.afs.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.afs.service.model.bo.AfsOrderQueryBO;
import com.medusa.gruul.afs.service.model.dto.AfsPageDTO;
import com.medusa.gruul.afs.service.model.dto.AfsQueryDTO;
import com.medusa.gruul.afs.service.mp.entity.AfsOrder;

import java.util.List;

/**
 * <p>
 * 售后工单表 服务类
 * </p>
 *
 * @author 张治保
 * @since 2022-08-03
 */
public interface IAfsOrderService extends IService<AfsOrder> {

    /**
     * 售后工单列表 分页查询
     *
     * @param afsPage 分页查询参数
     * @return 分页查询结果
     */
    IPage<AfsOrder> afsOrderPage(AfsPageDTO afsPage);

    /**
     * 根据查询条件 查询售后详情
     *
     * @param afsOrderQuery 查询条件
     * @return 查询结果
     */
    List<AfsOrder> getAfsOrderDetail(AfsOrderQueryBO afsOrderQuery);

    /**
     * 售后工单统计
     *
     * @param afsQuery 查询条件
     * @return 统计到的数量
     */
    Integer staticsStatusCount(AfsQueryDTO afsQuery);
}
