package com.medusa.gruul.addon.bargain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.bargain.model.dto.BargainHelpPeopleQueryDTO;
import com.medusa.gruul.addon.bargain.model.vo.BargainHelpPeopleVO;

/**
 * @author wudi
 */
public interface BargainHelpPeopleManageService {
    /**
     * 获取砍价好友列表
     *
     * @param bargainHelpPeopleQuery 砍价好友列表查询参数
     * @return 砍价帮助列表
     */
    IPage<BargainHelpPeopleVO> getBargainHelpPeoplePage(BargainHelpPeopleQueryDTO bargainHelpPeopleQuery);
}
