package com.medusa.gruul.addon.bargain.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.bargain.model.dto.BargainHelpPeopleQueryDTO;
import com.medusa.gruul.addon.bargain.model.vo.BargainHelpPeopleVO;
import com.medusa.gruul.addon.bargain.mp.entity.BargainHelpPeople;
import com.medusa.gruul.addon.bargain.mp.mapper.BargainHelpPeopleMapper;
import com.medusa.gruul.addon.bargain.mp.service.IBargainHelpPeopleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * 帮砍表 服务实现类
 * @author WuDi
 * @since 2023-03-14
 */
@Service
public class BargainHelpPeopleServiceImpl extends ServiceImpl<BargainHelpPeopleMapper, BargainHelpPeople> implements IBargainHelpPeopleService {

    /**
     * 获取砍价好友列表
     *
     * @param bargainHelpPeopleQuery 砍价好友列表查询参数
     * @return 砍价帮助列表
     */
    @Override
    public IPage<BargainHelpPeopleVO> getBargainHelpPeoplePage(BargainHelpPeopleQueryDTO bargainHelpPeopleQuery) {
        return baseMapper.getBargainHelpPeoplePage(bargainHelpPeopleQuery);
    }
}
