package com.medusa.gruul.addon.matching.treasure.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.matching.treasure.model.dto.SetMealQueryDTO;
import com.medusa.gruul.addon.matching.treasure.model.vo.SetMealVO;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMeal;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 套餐表 Mapper 接口
 * 
 *
 * @author WuDi
 * @since 2023-02-27
 */
public interface SetMealMapper extends BaseMapper<SetMeal> {

    /**
     * 分页查询套餐活动
     *
     * @param setMealQuery 查询参数
     * @return 分页结果
     */
    IPage<SetMealVO> pageSetMeal(@Param("setMealQuery") SetMealQueryDTO setMealQuery);

}
