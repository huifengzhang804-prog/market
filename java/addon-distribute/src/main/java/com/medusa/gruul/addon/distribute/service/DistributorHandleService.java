package com.medusa.gruul.addon.distribute.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.distribute.model.dto.ApplyAffairsDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorQueryDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorRankDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorTeamQueryDTO;
import com.medusa.gruul.addon.distribute.model.enums.DistributorStatus;
import com.medusa.gruul.addon.distribute.model.vo.DistributorApplyStatusVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorAuditVo;
import com.medusa.gruul.addon.distribute.model.vo.DistributorRankPageVO;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import com.medusa.gruul.service.uaa.api.dto.UserBaseDataDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import io.vavr.control.Option;

/**
 * @author 张治保
 * date 2022/11/17
 */
public interface DistributorHandleService {

    /**
     * 根据用户id获取分销商信息
     *
     * @param userId 用户id
     * @return 分销商信息
     */
    Distributor getAffairsInfoByUserId(Long userId);

    /**
     * 根据用户id查询用户分销码
     *
     * @param userId 用户 id
     * @return 可能为空分销码
     */
    Option<String> getCode(Long userId);

    /**
     * 根据用户 Id 获取分销商申请信息
     *
     * @param userId 用户 id
     * @return 可能为空的分销商申请信息
     */
    DistributorApplyStatusVO applyStatus(Long userId);

    /**
     * 根据用户id获取分销商/分销员详情
     *
     * @param userId 用户id
     * @return 结果可能为空
     */
    Option<Distributor> getByUserId(Long userId);


    /**
     * 判断用户是否已是分销商
     *
     * @param userId 用户id
     * @return 是否是分销商
     */
    boolean isAffairs(Long userId);

    /**
     * 用户扫码之后成为分销员 推荐人为当前code所属分销商
     *
     * @param userId 用户id
     * @param code   推荐人 专属 分销码
     */
    void scanCode(Long userId, String code);


    /**
     * 提交申请成为分销商的表单
     *
     * @param userId       用户id
     * @param openid       用户openId 服务商模式强制要求必须有openid
     * @param applyAffairs 申请表单
     */
    void applyAffairs(Long userId, String openid, ApplyAffairsDTO applyAffairs);

    /**
     * 分页查询 分销商
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    IPage<Distributor> distributorPage(DistributorQueryDTO query);

    /**
     * 分页查询分销排行榜
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    DistributorRankPageVO rank(DistributorRankDTO query);

    /**
     * 根据分销商用户id 分页查询分销商团队
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    IPage<Distributor> distributorTeamPage(DistributorTeamQueryDTO query);


    /**
     * 修改目标用户的分销商申请状态
     *
     * @param userId        申请所属用户id
     * @param currentStatus 当前申请状态
     * @param targetStatus  目标状态
     */
    void updateDistributorStatus(Long userId, DistributorStatus currentStatus, DistributorStatus targetStatus);


    /**
     * 根据用户id查询上级分销商信息
     *
     * @param userId 用户id
     * @return 可能为空的分销商信息
     */
    Option<Distributor> getParentDistributorByUserId(Long userId);


    /**
     * 更新店铺信息
     *
     * @param shopInfo 店铺信息
     */
    void updateShopInfo(ShopInfoVO shopInfo);


    /**
     * 更新分销用户资料
     *
     * @param userData 用户资料
     */
    void updateUserData(UserBaseDataDTO userData);

    /**
     * 处理申请
     *
     * @param distributorAuditVo
     */
    void applyAudit(DistributorAuditVo distributorAuditVo);

    /**
     * 查询被拒绝的原因
     *
     * @param id
     * @return
     */
    String queryRejectReason(Long id);


}
