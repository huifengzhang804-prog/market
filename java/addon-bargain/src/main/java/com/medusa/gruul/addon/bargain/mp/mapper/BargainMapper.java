package com.medusa.gruul.addon.bargain.mp.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.bargain.model.dto.BargainQueryDTO;
import com.medusa.gruul.addon.bargain.model.vo.BargainInfoVO;
import com.medusa.gruul.addon.bargain.mp.entity.Bargain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 砍价活动表 Mapper 接口
 * 
 *
 * @author WuDi
 * @since 2023-03-14
 */
public interface BargainMapper extends BaseMapper<Bargain> {

    /**
     * 分页查询砍价活动
     * @param bargainQuery 砍价活动查询条件
     * @return 砍价活动分页
     */
    IPage<BargainInfoVO> bargainInfoPage(@Param("bargainQuery") BargainQueryDTO bargainQuery);
}
