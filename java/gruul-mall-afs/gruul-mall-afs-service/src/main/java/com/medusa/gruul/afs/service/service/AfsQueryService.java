package com.medusa.gruul.afs.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.afs.service.model.dto.AfsPageDTO;
import com.medusa.gruul.afs.service.model.dto.AfsQueryDTO;
import com.medusa.gruul.afs.service.mp.entity.AfsHistory;
import com.medusa.gruul.afs.service.mp.entity.AfsOrder;
import com.medusa.gruul.afs.service.mp.entity.AfsOrderItem;
import io.vavr.control.Option;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/8/9
 */
public interface AfsQueryService {
    /**
     * 售后工单分野查询
     *
     * @param afsPage 分页查询条件
     * @return 分页查询结果
     */
    IPage<AfsOrder> afsOrderPage(AfsPageDTO afsPage);

    /**
     * 根据售后工单号查询售后工单详情
     *
     * @param afsNo 售后工单号
     * @return 售后工单详情
     */
    Option<AfsOrder> getCurrentAfsOrderDetail(String afsNo);

    /**
     * 根据售后工单号查询售后历史
     *
     * @param afsNo 售后工单号
     * @return 售后历史列表
     */
    List<AfsOrder> getAfsHistory(String afsNo);


    /**
     * 根据售后工单号查询售后商品项
     *
     * @param afsNo 售后工单号
     * @return 商品项列表
     */
    List<AfsOrderItem> afsOrderItems(String afsNo);

    /**
     * 批量查询工单的最新一条的售后历史
     *
     * @param afsNo 售后工单号
     * @return 工单map
     */
    Map<String, AfsHistory> lastAfsHistory(Set<String> afsNo);

    /**
     * 查询售后工单状态统计
     *
     * @param afsQueryDTO
     * @return
     */
    Integer staticsStatusCount(AfsQueryDTO afsQueryDTO);
}
