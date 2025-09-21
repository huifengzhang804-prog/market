package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.user.api.constant.UserConstant;
import com.medusa.gruul.user.api.model.dto.MemberLabelDTO;
import com.medusa.gruul.user.api.model.dto.RelevancyRightsDTO;
import com.medusa.gruul.user.service.model.dto.UserFreeMemberDTO;
import com.medusa.gruul.user.service.model.vo.FreeMemberRightsVO;
import com.medusa.gruul.user.service.mp.entity.UserFreeMember;
import com.medusa.gruul.user.service.mp.entity.UserMemberRelevancyRights;
import com.medusa.gruul.user.service.mp.service.IUserAccountService;
import com.medusa.gruul.user.service.mp.service.IUserFreeMemberService;
import com.medusa.gruul.user.service.mp.service.IUserMemberRelevancyRightsService;
import com.medusa.gruul.user.service.service.FreeMemberService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 免费会员实现层
 *
 * @author xiaoq
 * Description FreeMemberServiceImpl.java
 * date 2022-11-10 18:44
 */
@Service
@RequiredArgsConstructor
public class FreeMemberServiceImpl implements FreeMemberService {

    private final IUserFreeMemberService userFreeMemberService;

    private final IUserMemberRelevancyRightsService userMemberRelevancyRightsService;

    private final IUserAccountService userAccountService;

    /**
     * 免费会员新增
     *
     * @param userFreeMemberDTO 免费会员dto
     */
    @Override
    @Redisson(name = UserConstant.NEW_FREE_MEMBER_CARD)
    @Transactional(rollbackFor = Exception.class)
    public void addFreeMember(UserFreeMemberDTO userFreeMemberDTO) {
        //获取最高等级会员设置信息
        UserFreeMember preFreeMember = userFreeMemberService
                .lambdaQuery()
                .orderByDesc(UserFreeMember::getRankCode)
                .last(CommonPool.SQL_LIMIT_1)
                .one();
        int topRank = Option.of(preFreeMember).map(UserFreeMember::getRankCode).getOrElse(CommonPool.NUMBER_ZERO);
        if (topRank >= CommonPool.NUMBER_TEN) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "免费会员最大等级为10级");
        }
        if (topRank > CommonPool.NUMBER_ONE && userFreeMemberDTO.getNeedValue() <= preFreeMember.getNeedValue()) {
            throw new GlobalException("当前会员成长值应大于"+preFreeMember.getNeedValue()+"点成长值");
        }
        UserFreeMember userFreeMember = new UserFreeMember();
        userFreeMember.setFreeMemberName(userFreeMemberDTO.getFreeMemberName());
        userFreeMember.setNeedValue(userFreeMemberDTO.getNeedValue());
        userFreeMember.setRankCode(topRank + 1);
        userFreeMemberService.save(userFreeMember);
        saveRelevancyRights(userFreeMemberDTO.getRelevancyRightsList(), userFreeMember.getId());
    }

    /**
     * 免费会员更新 先更新会员信息在更新会员关联权益信息
     *
     * @param userFreeMemberDTO 免费会员dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFreeMember(UserFreeMemberDTO userFreeMemberDTO) {
        UserFreeMember userFreeMember = userFreeMemberService.lambdaQuery().eq(BaseEntity::getId, userFreeMemberDTO.getId()).one();
        if (userFreeMember == null) {
            throw new GlobalException("当前会员级别信息不存在");
        }
        if (userFreeMember.getRankCode().equals(CommonPool.NUMBER_ONE) && !userFreeMember.getNeedValue().equals(userFreeMemberDTO.getNeedValue())) {
            throw new GlobalException("当前会员级别信息不可修改成长值");
        }
        //如修改了成长值应比较所需成长值
        if (!userFreeMember.getNeedValue().equals(userFreeMemberDTO.getNeedValue())) {
            List<UserFreeMember> userFreeMemberList = userFreeMemberService.lambdaQuery().orderByAsc(UserFreeMember::getRankCode).list();
            //当会员等级级别数量只有一条时可任意修改
            if (userFreeMemberList.size() > CommonPool.NUMBER_ONE) {
                // 修改会员成长值时进行比较
                freeMemberCompare(userFreeMember, userFreeMemberList, userFreeMemberDTO.getNeedValue());
            }
        }
        BeanUtil.copyProperties(userFreeMemberDTO, userFreeMember);
        userFreeMemberService.updateById(userFreeMember);
        //清除免费会员关联的权益
        userMemberRelevancyRightsService
                .lambdaUpdate()
                .eq(UserMemberRelevancyRights::getMemberId, userFreeMember.getId())
                .remove();
        saveRelevancyRights(userFreeMemberDTO.getRelevancyRightsList(), userFreeMember.getId());
    }

    /**
     * 会员删除
     *
     * @param id 会员id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delFreeMember(Long id) {
        //获取最高等级会员设置信息
        UserFreeMember userFreeMemberTop = userFreeMemberService
                .lambdaQuery()
                .orderByDesc(UserFreeMember::getRankCode)
                .last(CommonPool.SQL_LIMIT_1).one();
        if (!userFreeMemberTop.getId().equals(id)) {
            throw new GlobalException("请确认当前会员等级是否存在,并逐级删除会员等级");
        }
        if (id == (CommonPool.NUMBER_ONE)) {
            throw new GlobalException("默认会员不可删除");
        }
        UserFreeMember one = userFreeMemberService.lambdaQuery()
                .eq(UserFreeMember::getId, id).one();
        if (Objects.isNull(one)) {
            throw new GlobalException("当前会员级别信息不存在");
        }
        Boolean inUse=userAccountService.checkFreeMemberInUse(one.getNeedValue());
        if (inUse) {
            throw new GlobalException("该会员等级有用户，无法删除");
        }
        userFreeMemberService.lambdaUpdate().eq(BaseEntity::getId, id).remove();
        //清空该会员等级所属权益
        userMemberRelevancyRightsService.lambdaUpdate()
                .eq(UserMemberRelevancyRights::getMemberId, id)
                .remove();

    }

    /**
     * 免费会员列表
     *
     * @return List<免费级别会员权益VO>
     */
    @Override
    public List<FreeMemberRightsVO> getFreeMemberList() {
        return userFreeMemberService.getFreeMemberList();
    }

    /**
     * 获取初始免费会员信息
     *
     * @return 会员信息
     */
    @Override
    public UserFreeMember getInitialFreeMemberInfo() {
        return userFreeMemberService.lambdaQuery().eq(UserFreeMember::getRankCode, CommonPool.NUMBER_ONE).one();
    }

    /**
     * 获取用户所处会员级别
     *
     * @param growthValue 用户成长值
     * @return 免费级别会员权益VO
     */
    @Override
    public FreeMemberRightsVO getCurrentMemberRank(Long growthValue) {
        return userFreeMemberService.getCurrentMemberRank(growthValue);
    }

    /**
     * 获取会员级别权益vo
     *
     * @param id 免费会员级别id
     * @return 免费级别会员权益VO
     */
    @Override
    public FreeMemberRightsVO getFreeMemberInfo(Long id) {
        return userFreeMemberService.getFreeMemberInfo(id);
    }

    /**
     * 获取下一级或当前等级的会员级别信息
     *
     * @param rankCode 当前会员级别
     * @return 下一级或当前等级的会员级别信息
     */
    @Override
    public UserFreeMember getNextLevelMemberInfo(Integer rankCode) {
        Integer nextRankCode = rankCode + CommonPool.NUMBER_ONE;
        UserFreeMember userFreeMember = userFreeMemberService.lambdaQuery()
                .eq(UserFreeMember::getRankCode, nextRankCode).one();
        if (userFreeMember == null) {
            userFreeMember = userFreeMemberService.lambdaQuery().eq(UserFreeMember::getRankCode, rankCode).one();
        }
        return userFreeMember;
    }

    /**
     * 免费会员设置标签
     *
     * @param dto 会员标签dto
     */
    @Override
    public void addFreeMemberLabel(MemberLabelDTO dto) {
        UserFreeMember userFreeMember = userFreeMemberService.lambdaQuery().eq(BaseEntity::getId, dto.getId()).one();
        if (userFreeMember == null) {
            throw new GlobalException("当前会员级别信息不存在");
        }
        userFreeMember.setLabelJson(dto);
        userFreeMemberService.updateById(userFreeMember);
    }

    /**
     * 保存会员关联权益数据
     *
     * @param relevancyRights 会员关联权益数据
     * @param id              会员级别id
     */
    private void saveRelevancyRights(List<RelevancyRightsDTO> relevancyRights, Long id) {
        if (CollUtil.isEmpty(relevancyRights)) {
            return;
        }
        userMemberRelevancyRightsService.saveBatch(
                relevancyRights.stream()
                        .map(
                                relevancyRight -> new UserMemberRelevancyRights()
                                        .setMemberId(id)
                                        .setExtendValue(relevancyRight.getExtendValue())
                                        .setMemberRightsId(relevancyRight.getMemberRightsId())
                        )
                        .toList()
        );
    }


    /**
     * 会员成长值比较
     *
     * @param userFreeMember     修改会员信息
     * @param userFreeMemberList 会员排序信息
     * @param needValue          修改的成长值
     */
    private void freeMemberCompare(UserFreeMember userFreeMember, List<UserFreeMember> userFreeMemberList, Long needValue) {
        // 获取低一级 or (自身会员等级数据 应为最低价会员数据)
        UserFreeMember lowOneLevelUserFreeMember = userFreeMemberList
                .get(userFreeMember.getRankCode() > CommonPool.NUMBER_ONE ?
                        userFreeMember.getRankCode() - CommonPool.NUMBER_TWO :
                        userFreeMember.getRankCode() - CommonPool.NUMBER_ONE);
        // 获取高一级 or (自身员等级数据 应为最高级会员数据)
        UserFreeMember highOneLevelUserFreeMember = userFreeMemberList
                .get(userFreeMember.getRankCode() < userFreeMemberList.size() ?
                        userFreeMember.getRankCode() :
                        userFreeMemberList.size() - CommonPool.NUMBER_ONE);
        UserFreeMember checkFreeMember;

        if (userFreeMember.getRankCode().equals(lowOneLevelUserFreeMember.getRankCode())) {
            // 判断高一级
            checkFreeMember = userFreeMemberList.get(lowOneLevelUserFreeMember.getRankCode());
            if (needValue >= checkFreeMember.getNeedValue()) {
                throw new GlobalException("当前会员成长值不可大于"+checkFreeMember.getNeedValue()+"点成长值");
            }
            return;
        }

        if (userFreeMember.getRankCode().equals(highOneLevelUserFreeMember.getRankCode())) {
            //判断低一级
            checkFreeMember = userFreeMemberList.get(highOneLevelUserFreeMember.getRankCode() - CommonPool.NUMBER_TWO);
            if (needValue <= checkFreeMember.getNeedValue()) {
                throw new GlobalException("当前会员成长值不可小于"+checkFreeMember.getNeedValue()+"点成长值");
            }
            return;
        }

        // 判断低一级 and 高一级
        if (needValue <= lowOneLevelUserFreeMember.getNeedValue()) {
            throw new GlobalException("当前会员成长值不可小于"+lowOneLevelUserFreeMember.getNeedValue()+"点成长值");
        }
        if (needValue >= highOneLevelUserFreeMember.getNeedValue()) {
            throw new GlobalException("当前会员成长值不可大于"+highOneLevelUserFreeMember.getNeedValue()+"点成长值");
        }
    }
}
