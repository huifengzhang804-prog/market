package com.medusa.gruul.addon.bargain.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.bargain.model.dto.BargainHelpPeopleQueryDTO;
import com.medusa.gruul.addon.bargain.model.vo.BargainHelpPeopleVO;
import com.medusa.gruul.addon.bargain.mp.entity.BargainHelpPeople;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * 帮砍表 服务类
 *
 * @author WuDi
 * @since 2023-03-14
 */
public interface IBargainHelpPeopleService extends IService<BargainHelpPeople> {

    /**
     * 获取砍价好友列表
     *
     * @param bargainHelpPeopleQuery 砍价好友列表查询参数
     * @return 砍价帮助列表
     */
    IPage<BargainHelpPeopleVO> getBargainHelpPeoplePage(BargainHelpPeopleQueryDTO bargainHelpPeopleQuery);
}
