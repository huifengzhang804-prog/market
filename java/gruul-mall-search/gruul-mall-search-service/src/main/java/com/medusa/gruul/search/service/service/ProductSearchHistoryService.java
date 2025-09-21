package com.medusa.gruul.search.service.service;

import com.medusa.gruul.search.service.model.vo.HistoriesAndHotWordsVO;
import io.vavr.control.Option;

/**
 * @author 张治保
 * date 2022/12/15
 */
public interface ProductSearchHistoryService {

    /**
     * 用户搜索建议
     *
     * @param userIdOpt 可能为空的用户id
     * @return 搜索建议
     */
    HistoriesAndHotWordsVO historiesAndHotWords(Option<Long> userIdOpt);

    /**
     * 保存 搜索词
     *
     * @param userIdOpt 可能为空的用户id
     * @param keyword   搜索词
     */
    void saveSearchKeywords(Option<Long> userIdOpt, String keyword);

    /**
     * 根据用户 id 删除用户搜索历史
     *
     * @param userId 用户id
     */
    void deleteHistoryByUserId(Long userId);
}
