package com.medusa.gruul.addon.matching.treasure.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.matching.treasure.model.dto.SetMealQueryDTO;
import com.medusa.gruul.addon.matching.treasure.model.enums.SetMealStatus;
import com.medusa.gruul.addon.matching.treasure.model.vo.SetMealVO;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMeal;
import com.medusa.gruul.addon.matching.treasure.mp.mapper.SetMealMapper;
import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealService;
import com.medusa.gruul.common.model.constant.CommonPool;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 *
 * 套餐表 服务实现类
 * 
 *
 * @author WuDi
 * @since 2023-02-27
 */
@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements ISetMealService {

    /**
     * 分页查询套餐活动
     *
     * @param setMealQuery 查询参数
     * @return 分页结果
     */
    @Override
    public IPage<SetMealVO> pageSetMeal(SetMealQueryDTO setMealQuery) {

        IPage<SetMealVO> page = baseMapper.pageSetMeal(setMealQuery);
        if (page.getTotal() < CommonPool.NUMBER_ONE){
            return page;
        }
      List<SetMealVO> records = page.getRecords();


      records.forEach(
                record->{
                    record.setSetMealTypeDesc(record.getSetMealType().getDesc());
                    SetMealStatus status = record.getSetMealStatus();
                    record.setSetMealStatus(
                            switch (status){
                                case ILLEGAL_SELL_OFF,MERCHANT_SELL_OFF -> status;
                                default -> {
                                    LocalDateTime now = LocalDateTime.now();
                                    LocalDateTime startTime = record.getStartTime();
                                    if (now.isBefore(startTime)){
                                        yield  SetMealStatus.NOT_STARTED;
                                    }
                                    if (now.isBefore(record.getEndTime())){
                                        yield SetMealStatus.PROCESSING;
                                    }
                                    yield  SetMealStatus.OVER;
                                }
                            }
                    );
                }
        );
        return page;
    }

}
