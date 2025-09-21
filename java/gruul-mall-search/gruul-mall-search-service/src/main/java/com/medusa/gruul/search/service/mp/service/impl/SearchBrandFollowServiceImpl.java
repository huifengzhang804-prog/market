package com.medusa.gruul.search.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.search.service.mp.entity.SearchBrandFollow;
import com.medusa.gruul.search.service.mp.mapper.SearchBrandFollowMapper;
import com.medusa.gruul.search.service.mp.service.ISearchBrandFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 品牌关注表 服务实现类
 * </p>
 *
 * @author WuDi
 * @since 2023-02-02
 */
@Service
@RequiredArgsConstructor
public class SearchBrandFollowServiceImpl extends ServiceImpl<SearchBrandFollowMapper, SearchBrandFollow> implements ISearchBrandFollowService {

}
