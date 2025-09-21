package com.medusa.gruul.addon.member.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.member.model.dto.PaidMemberDTO;
import com.medusa.gruul.addon.member.model.vo.PaidMemberRightsVO;
import com.medusa.gruul.addon.member.mp.entity.PaidMember;
import com.medusa.gruul.addon.member.mp.entity.PaidMemberRelevancyRights;
import com.medusa.gruul.addon.member.mp.service.IPaidMemberRelevancyRightsService;
import com.medusa.gruul.addon.member.mp.service.IPaidMemberService;
import com.medusa.gruul.addon.member.service.ManagePaidMemberService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.user.api.enums.paid.EffectiveDurationType;
import com.medusa.gruul.user.api.model.dto.MemberLabelDTO;
import com.medusa.gruul.user.api.model.dto.RelevancyRightsDTO;
import com.medusa.gruul.user.api.model.dto.paid.PaidRuleJsonDTO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理端付费会员实现层
 *
 * @author xiaoq
 * @Description ManagePaidMemberServiceImpl.java
 * @date 2022-11-15 11:34
 */
@Service
@RequiredArgsConstructor
public class ManagePaidMemberServiceImpl implements ManagePaidMemberService {
    private final IPaidMemberService paidMemberService;
    private final IPaidMemberRelevancyRightsService paidMemberRelevancyRightsService;
    private final UserRpcService userRpcService;

    /**
     * 付费会员新增 不校验权益信息 价格信息
     *
     * @param paidMemberDTO 付费会员信息
     */
    @Override
    public void addPaidMember(PaidMemberDTO paidMemberDTO) {
        Integer rankCode = CommonPool.NUMBER_ZERO;
        //或者最高级别的会员信息
        PaidMember paidMemberTop = paidMemberService.lambdaQuery()
                .orderByDesc(PaidMember::getRankCode)
                .last(CommonPool.SQL_LIMIT_1)
                .one();
        if (paidMemberTop != null) {
            rankCode = paidMemberTop.getRankCode();
            if (rankCode >= CommonPool.NUMBER_FIVE) {
                throw new GlobalException("当前付费会员等级不可超过5");
            }
        }
        //判断是否有重复枚举
        checkEffectiveDurationType(paidMemberDTO);
        List<PaidRuleJsonDTO> collect = paidMemberDTO.getPaidRuleJson()
                .stream()
                .peek((paidRuleJson) -> paidRuleJson.setId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(new PaidMember()).longValue())).collect(Collectors.toList());
        PaidMember paidMember = new PaidMember();
        paidMember.setRankCode(++rankCode);
        paidMember.setPaidMemberName(paidMemberDTO.getPaidMemberName());
        paidMember.setPaidRuleJson(collect);
        paidMemberService.save(paidMember);
        // 新增权益信息
        savePaidMemberRelevancyRights(paidMemberDTO.getRelevancyRightsList(), paidMember.getId());
    }


    /**
     * 付费会员修改 不校验权益信息 价格信息
     *
     * @param paidMemberDTO 付费会员信息
     */
    @Override
    public void updatePaidMember(PaidMemberDTO paidMemberDTO) {
        PaidMember paidMember = paidMemberService.lambdaQuery().eq(BaseEntity::getId, paidMemberDTO.getId()).one();
        if (paidMember == null) {
            throw new GlobalException("当前会员级别信息不存在");
        }
        checkEffectiveDurationType(paidMemberDTO);
        List<PaidRuleJsonDTO> collect = paidMemberDTO.getPaidRuleJson()
                .stream()
                .peek((paidRuleJson) -> paidRuleJson.setId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(new PaidMember()).longValue())).collect(Collectors.toList());
        paidMemberService.lambdaUpdate()
                .set(PaidMember::getPaidMemberName, paidMemberDTO.getPaidMemberName())
                .set(PaidMember::getPaidRuleJson, JSON.toJSONString(collect))
                .eq(BaseEntity::getId, paidMemberDTO.getId())
                .update();
        //清除付费会员关联的权益
        paidMemberRelevancyRightsService.lambdaUpdate().eq(PaidMemberRelevancyRights::getPaidMemberId, paidMemberDTO.getId()).remove();
        savePaidMemberRelevancyRights(paidMemberDTO.getRelevancyRightsList(), paidMember.getId());
    }

    /**
     * 付费会员删除
     *
     * @param id 付费会员
     */
    @Override
    public void delPaidMember(Long id) {
        PaidMember paidMember = paidMemberService.lambdaQuery().eq(BaseEntity::getId, id).one();
        if (paidMember == null) {
            throw new GlobalException("当前会员不存在");
        }
        PaidMember paidMemberTop = paidMemberService.lambdaQuery()
                .orderByDesc(PaidMember::getRankCode)
                .last(CommonPool.SQL_LIMIT_1)
                .one();
        if (!paidMemberTop.getId().equals(id)) {
            throw new GlobalException("请逐级删除会员等级");
        }
        boolean inUse = userRpcService.queryMemberInUse(paidMember.getId());
        if (inUse) {
            throw new GlobalException("该会员等级有用户，无法删除");
        }

        boolean remove = paidMemberService.lambdaUpdate().eq(BaseEntity::getId, id).ne(PaidMember::getRankCode, CommonPool.NUMBER_ONE).remove();
        if (!remove) {
            throw new GlobalException(SystemCode.DATA_DELETE_FAILED_CODE, "付费会员级别删除失败 请检查删除会员是否为LV1 ");
        }
        //清空该会员等级所属权益
        paidMemberRelevancyRightsService.lambdaUpdate()
                .eq(PaidMemberRelevancyRights::getPaidMemberId, id)
                .remove();
    }


    /**
     * 付费会员级别信息
     *
     * @return List<付费会员权益VO>
     */
    @Override
    public List<PaidMemberRightsVO> getPaidMemberList() {
        List<PaidMemberRightsVO> paidMemberList = paidMemberService.getPaidMemberList();
        if (CollUtil.isEmpty(paidMemberList)) {
            return Collections.emptyList();
        }
        disposeRights(paidMemberList);
        return paidMemberList;
    }


    @Override
    public PaidMemberRightsVO getPaidMemberInfo(Long id) {
        PaidMemberRightsVO paidMemberRights = paidMemberService.getPaidMemberInfo(id);
        if (paidMemberRights == null) {
            throw new GlobalException("当前付费会员级别不存在");
        }
        List<PaidMemberRightsVO> paidMemberRightsList = new ArrayList<>();
        paidMemberRightsList.add(paidMemberRights);
        disposeRights(paidMemberRightsList);
        return paidMemberRights;
    }

    /**
     * 付费会员标签设置
     *
     * @param dto 标签信息
     */
    @Override
    public void savePaidMemberLabel(MemberLabelDTO dto) {
        PaidMemberRightsVO paidMemberRights = paidMemberService.getPaidMemberInfo(dto.getId());
        if (paidMemberRights == null) {
            throw new GlobalException("当前付费会员级别不存在");
        }
        PaidMember paidMember = new PaidMember();
        paidMember.setId(paidMemberRights.getId());
        paidMember.setLabelJson(dto);
        paidMemberService.updateById(paidMember);
    }

    /**
     * 保存付费会员关联权益数据
     *
     * @param relevancyRightsList 付费会员关联权益数据
     * @param id                  付费会员级别id
     */
    private void savePaidMemberRelevancyRights(List<RelevancyRightsDTO> relevancyRightsList, Long id) {
        if (CollUtil.isNotEmpty(relevancyRightsList)) {
            List<PaidMemberRelevancyRights> paidMemberRelevancyRights = new ArrayList<>();
            relevancyRightsList.forEach(
                    bean -> paidMemberRelevancyRights.add(new PaidMemberRelevancyRights()
                            .setPaidMemberId(id)
                            .setExtendValue(bean.getExtendValue())
                            .setMemberRightsId(bean.getMemberRightsId()))
            );
            paidMemberRelevancyRightsService.saveBatch(paidMemberRelevancyRights);
        }
    }


    @Override
    public void disposeRights(List<PaidMemberRightsVO> paidMemberList) {
        Map<Long, Set<Long>> rankPaidMember = paidMemberList.stream()
                .collect(
                        Collectors.toMap(
                                PaidMemberRightsVO::getId,
                                paidMemberRights -> paidMemberRights.getRelevancyRightsList().stream().map(RelevancyRightsVO::getMemberRightsId).collect(Collectors.toSet())
                        )
                );
        Map<Long, List<RelevancyRightsVO>> paidMemberRelevancyInfo = userRpcService.getPaidMemberRelevancyInfo(rankPaidMember);
        // 遍历补充数据
        for (PaidMemberRightsVO paidMemberRights : paidMemberList) {
            paidMemberRights.getRelevancyRightsList().forEach(relevancyRightsVO -> {
                Optional<RelevancyRightsVO> any = paidMemberRelevancyInfo.get(paidMemberRights.getId()).stream()
                        .filter(bean -> bean.getMemberRightsId().equals(relevancyRightsVO.getMemberRightsId()))
                        .findAny();
                any.ifPresent(rightsVO -> relevancyRightsVO
                        .setRightsExplain(rightsVO.getRightsExplain())
                        .setRightsIcon(rightsVO.getRightsIcon())
                        .setRightsName(rightsVO.getRightsName())
                        .setRightsType(rightsVO.getRightsType()));
            });
            // 过滤已关闭的会员权益
            paidMemberRights.getRelevancyRightsList().removeIf(bean -> bean.getRightsType() == null);
        }
    }


    /**
     * 检测付费会员有效期是否重复
     *
     * @param paidMemberDTO 付费会员DTO
     */
    private void checkEffectiveDurationType(PaidMemberDTO paidMemberDTO) {
        //判断是否有重复枚举
        if (paidMemberDTO.getPaidRuleJson().size() > CommonPool.NUMBER_ONE) {
            Set<EffectiveDurationType> typeSet = paidMemberDTO.getPaidRuleJson().stream().map(PaidRuleJsonDTO::getEffectiveDurationType).collect(Collectors.toSet());
            if (paidMemberDTO.getPaidRuleJson().size() != typeSet.size()) {
                throw new GlobalException("请检查是否有重复设置付费会员有效时长！！！");
            }
        }
    }


}