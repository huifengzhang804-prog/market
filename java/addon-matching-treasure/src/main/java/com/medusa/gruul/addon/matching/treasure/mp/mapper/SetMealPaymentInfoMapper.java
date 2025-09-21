package com.medusa.gruul.addon.matching.treasure.mp.mapper;

import com.medusa.gruul.addon.matching.treasure.model.dto.SetMealProductStats;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMealPaymentInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author WuDi
 * @since 2023-03-21
 */
public interface SetMealPaymentInfoMapper extends BaseMapper<SetMealPaymentInfo> {

  /**
   * 查询套餐订单数量
   * @param setMealIds
   * @return
   */
  List<SetMealProductStats> querySetMealOrderCount(@Param("setMealIds") Set<Long> setMealIds);
}
