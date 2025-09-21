package com.medusa.gruul.user.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.payment.api.model.dto.PayNotifyResultDTO;
import com.medusa.gruul.service.uaa.api.dto.UserAuthorityDTO;
import com.medusa.gruul.service.uaa.api.dto.UserRegisterDataDTO;
import com.medusa.gruul.user.api.model.dto.UserDataDTO;
import com.medusa.gruul.user.api.model.vo.UserPersonVo;
import com.medusa.gruul.user.service.model.dto.UserBlacklistQueryDTO;
import com.medusa.gruul.user.service.model.dto.UserGrowthValueDTO;
import com.medusa.gruul.user.service.model.dto.UserQueryDTO;
import com.medusa.gruul.user.service.model.vo.UserListVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author: WuDi
 * @date: 2022/9/20
 */
public interface UserManageService {

    /**
     * 更新用户信息
     *
     * @param userData 用户信息
     */
    void updateUser(UserDataDTO userData);


    /**
     * 用户余额变化
     *
     * @param dataChangeMessage 余额变化消息体
     */
    void userBalanceChange(DataChangeMessage dataChangeMessage);

    /**
     * 会员余额充值
     *
     * @param payNotifyResultDTO PayNotifyResultDTO.java
     */
    void userBalanceRecharge(PayNotifyResultDTO payNotifyResultDTO);

    /**
     * 获取用户余额
     *
     * @return 用户余额
     */
    Long getUserBalance();


    /**
     * 今日访客数
     *
     * @return 今日访客数数量
     */
    Long getTodayUvNum();

    /**
     * 订单完成更新用户交易总额
     *
     * @param orderCompletedDTO 订单完成
     */
    void updateUserTradeTotalAmount(OrderCompletedDTO orderCompletedDTO);

    /**
     * 获取用户详细信息
     *
     * @param id 用户表id
     * @return UserListVO
     */
    UserListVO getUserParticulars(Long id);

    /**
     * 用户会员列表数据
     *
     * @param userQuery 会员列表查询参数
     * @return 用户会员列表VO
     */
    IPage<UserListVO> getUserList(UserQueryDTO userQuery);

    /**
     * 用户权限编辑
     *
     * @param userAuthority 用户权限dto
     */
    void editMemberAuthority(UserAuthorityDTO userAuthority);

    /**
     * 用户黑名单列表查询
     *
     * @param userBlacklistQuery 用户黑名单查询dto
     * @return IPage<UserListVO>
     */
    IPage<UserListVO> getUserBlacklist(UserBlacklistQueryDTO userBlacklistQuery);

    /**
     * 用户个人中心数据
     *
     * @param userId userId
     * @return 用户个人中心数据
     */
    UserPersonVo userPerson(Long userId);

    /**
     * 批量注册用户
     *
     * @param userDataList 用户注册信息
     */
    void userBatchRegister(List<UserRegisterDataDTO> userDataList);

    /**
     * 批量导出用户为excel
     *
     * @param response  响应
     * @param userQuery 用户查询参数
     */
    void exportUser(HttpServletResponse response, UserQueryDTO userQuery);

    /**
     * 会员成长值调整
     *
     * @param userGrowthValue 用户成长值调整
     */
    void growthValueBalanceChange(UserGrowthValueDTO userGrowthValue);
}
