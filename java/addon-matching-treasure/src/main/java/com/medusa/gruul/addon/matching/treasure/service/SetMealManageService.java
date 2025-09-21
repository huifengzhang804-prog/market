package com.medusa.gruul.addon.matching.treasure.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.matching.treasure.model.dto.SetMealDTO;
import com.medusa.gruul.addon.matching.treasure.model.dto.SetMealQueryDTO;
import com.medusa.gruul.addon.matching.treasure.model.dto.ShopSetMealIdDTO;
import com.medusa.gruul.addon.matching.treasure.model.vo.SetMealVO;

import java.util.List;

public interface SetMealManageService {

    /**
     * 新增/编辑套餐
     *
     * @param shopId     店铺id
     * @param setMealDTO 套餐DTO
     */
    void addSetMeal(Long shopId, SetMealDTO setMealDTO);

    /**
     * 编辑套餐活动
     *
     * @param shopId     店铺id
     * @param setMealDTO 套餐DTO
     */
    void editSetMeal(Long shopId, SetMealDTO setMealDTO);

    /**
     * 查询套餐活动
     *
     * @param setMealQuery 查询参数
     * @return 分页结果
     */
    IPage<SetMealVO> pageSetMeal(SetMealQueryDTO setMealQuery);

    /**
     * 查询套餐活动详情
     *
     * @param shopId    店铺id
     * @param setMealId 套餐id
     * @return 套餐详情
     */
    SetMealVO getSetMealDetailById(Long shopId, Long setMealId);

    /**
     * 删除套餐活动
     *
     * @param shopId    店铺id
     * @param setMealId 套餐id
     */
    void deleteSetMeal(Long shopId, Long setMealId);

    /**
     * 批量删除套餐活动
     *
     * @param shopSetMealIds 店铺、套餐id集合
     */
    void deleteBatchSetMeal(List<ShopSetMealIdDTO> shopSetMealIds);

    /**
     * 下架套餐活动
     *
     * @param shopId    店铺id
     * @param setMealId 套餐id
     * @param violationExplain 违规说明
     */
    void sellOffSetMeal(Long shopId, Long setMealId, String violationExplain);


}
