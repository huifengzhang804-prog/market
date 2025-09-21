package com.medusa.gruul.user.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.user.api.enums.UserFootMarkStatus;
import com.medusa.gruul.user.api.model.GuessYouLikeVO;
import com.medusa.gruul.user.api.model.UserFootMarkVO;
import com.medusa.gruul.user.api.model.dto.UserFootMarkDTO;
import com.medusa.gruul.user.service.model.dto.GuessYouLikeQueryDTO;
import com.medusa.gruul.user.service.model.dto.UserFootMarkQueryDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 用户足迹
 *
 * @author: WuDi
 * @date: 2022/9/20
 */
public interface UserFootMarkManageService {

    /**
     * 添加用户足迹 （异步）
     *
     * @param userFootMark 用户足迹
     * @param userId       用户id
     */
    void addUserFootMark(UserFootMarkDTO userFootMark, Long userId);

    /**
     * 获取用户足迹
     *
     * @param userFootMarkQueryDTO 用户足迹查询条件
     * @return 查询结果
     */
    IPage<UserFootMarkVO> userFootMarkPage(UserFootMarkQueryDTO userFootMarkQueryDTO);

    /**
     * 足迹批量删除
     *
     * @param userFootMarkIds    足迹id
     * @param userFootMarkStatus 足迹枚举
     */
    void batchDeleteUserFootMark(Set<Long> userFootMarkIds, UserFootMarkStatus userFootMarkStatus);

    /**
     * 根据月份获取有足迹的日期
     *
     * @param month 月份
     * @return 有足迹的日期
     */
    List<LocalDate> getFootMarkBrowsDates(Integer month);

    /**
     * 猜你喜欢
     *
     * @param guessYouLikeQueryDTO 猜你喜欢查询参数
     * @return IPage<GuessYouLikeVO> 猜你喜欢
     */
    IPage<GuessYouLikeVO> guessYouLike(GuessYouLikeQueryDTO guessYouLikeQueryDTO);
}
