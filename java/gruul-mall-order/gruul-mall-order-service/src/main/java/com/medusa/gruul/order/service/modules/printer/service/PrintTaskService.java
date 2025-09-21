package com.medusa.gruul.order.service.modules.printer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.order.api.enums.PrintLink;
import com.medusa.gruul.order.service.modules.printer.model.dto.OrderPrintDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTaskDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTaskPageDTO;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.vo.PrintTaskRecordVO;

import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/8/22
 */
public interface PrintTaskService {

    /**
     * 新增或编辑打印任务
     *
     * @param shopId 店铺 id
     * @param param  打印任务参数
     */
    void saveOrUpdate(Long shopId, PrintTaskDTO param);

    /**
     * 分页查询打印任务
     *
     * @param shopId 店铺 id
     * @param param  查询参数
     * @return 分页查询结果
     */
    IPage<PrintTaskRecordVO> page(Long shopId, PrintTaskPageDTO param);


    /**
     * 根据任务 id删除打印任务
     *
     * @param shopId 店铺 id
     * @param id     打印任务 id
     */
    void delete(Long shopId, Long id);

    /**
     * 获取商家打印类型 某联（商家联、后厨联）
     *
     * @param shopId 店铺 id
     * @return 店铺打印类型
     * key 打印场景 同城、门店
     * value 打印类型集合
     */
    Map<PrintMode, Set<PrintLink>> printLinks(Long shopId);

    /**
     * 打印 订单小票
     *
     * @param param 打印的订单信息
     */
    void printOrder(OrderPrintDTO param);

    /**
     * 商家手动打印订单小票
     *
     * @param shopId  店铺 id
     * @param orderNo 订单号
     * @param link    打印类型（某联）
     */
    void printOrder(Long shopId, String orderNo, PrintLink link);
}
