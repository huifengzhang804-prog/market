package com.medusa.gruul.addon.matching.treasure.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.matching.treasure.model.dto.SetMealQueryDTO;
import com.medusa.gruul.addon.matching.treasure.model.vo.SetMealVO;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMeal;

/**
 *
 * 套餐表 服务类
 *
 * @author WuDi
 * @since 2023-02-27
 */
public interface ISetMealService extends IService<SetMeal> {
    /**
     * 查询套餐活动
     *
     * @param setMealQuery 查询参数
     * @return 分页结果
     */
    IPage<SetMealVO> pageSetMeal(SetMealQueryDTO setMealQuery);

}
