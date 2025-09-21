package com.medusa.gruul.addon.bargain.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.bargain.model.dto.BargainHelpPeopleQueryDTO;
import com.medusa.gruul.addon.bargain.model.vo.BargainHelpPeopleVO;
import com.medusa.gruul.addon.bargain.mp.service.IBargainHelpPeopleService;
import com.medusa.gruul.addon.bargain.service.BargainHelpPeopleManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author wudi
 */
@Service
@RequiredArgsConstructor
public class BargainHelpPeopleManageServiceImpl implements BargainHelpPeopleManageService {


    private final IBargainHelpPeopleService bargainHelpPeopleService;


    /**
     * 获取砍价好友列表
     *
     * @param bargainHelpPeopleQuery 砍价好友列表查询参数
     * @return 砍价帮助列表
     */
    @Override
    public IPage<BargainHelpPeopleVO> getBargainHelpPeoplePage(BargainHelpPeopleQueryDTO bargainHelpPeopleQuery) {
        return bargainHelpPeopleService.getBargainHelpPeoplePage(bargainHelpPeopleQuery);
    }
}
