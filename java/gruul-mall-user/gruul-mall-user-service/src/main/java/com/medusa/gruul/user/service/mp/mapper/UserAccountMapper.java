package com.medusa.gruul.user.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.user.service.model.dto.UserBlacklistQueryDTO;
import com.medusa.gruul.user.service.model.dto.UserQueryDTO;
import com.medusa.gruul.user.service.model.vo.RankedMemberVO;
import com.medusa.gruul.user.service.model.vo.UserListVO;
import com.medusa.gruul.user.service.model.vo.UserTagMapVO;
import com.medusa.gruul.user.service.mp.entity.UserAccount;
import com.medusa.gruul.user.service.mp.entity.UserMemberCard;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * Mapper 接口
 *
 * @author WuDi
 * @since 2022-09-13
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {


    /**
     * 新版本 分页查询会员信息
     *
     * @param query 会员列表查询参数
     * @return IPage<会员列表VO>
     * @author 张治保
     */
    IPage<UserListVO> userPage(@Param("query") UserQueryDTO query);

    /**
     * 根据用户id查询用户会员卡信息
     *
     * @param userIds 用户id集合
     * @return 会员卡信息集合
     * @author 张治保
     */
    List<UserMemberCard> memberCardByUserId(@Param("userIds") Set<Long> userIds);


    /**
     * 查询用户标签
     *
     * @param userIds 用户id集合
     * @return 用户id对应用户拥有的标签 列表
     * @author 张治保
     */
    List<UserTagMapVO> getTagsMapByUserIds(@Param("userIds") Set<Long> userIds);

    /**
     * 分页查询会员信息
     *
     * @param userQuery 会员列表查询参数
     * @return IPage<会员列表VO>
     */
    IPage<UserListVO> getUserList(@Param("userQuery") UserQueryDTO userQuery);

    /**
     * 根据用户id 获取用户详情
     *
     * @param userId 用户id
     * @return UserListVO
     */
    UserListVO getUserParticulars(@Param("userId") Long userId);

    /**
     * 用户黑名单列表查询
     *
     * @param userBlacklistQuery 用户黑名单查询dto
     * @return IPage<UserListVO>
     */
    IPage<UserListVO> getUserBlacklist(@Param("userBlacklistQuery") UserBlacklistQueryDTO userBlacklistQuery);


    /**
     * 根据用户id查询用户会员资料
     *
     * @param userId 用户id
     * @return 用户账号与会员卡数据
     */
    UserAccount getUserMemberInfoByUserId(@Param("userId") Long userId);

    List<RankedMemberVO> queryRankedMember(@Param("userIds") Set<Long> userIds);

}
