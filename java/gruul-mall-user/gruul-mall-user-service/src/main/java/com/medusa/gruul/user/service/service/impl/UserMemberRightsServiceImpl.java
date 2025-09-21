package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.enums.UserError;
import com.medusa.gruul.user.api.model.dto.MemberRightsDTO;
import com.medusa.gruul.user.service.mp.entity.MemberRights;
import com.medusa.gruul.user.service.mp.service.IMemberRightsService;
import com.medusa.gruul.user.service.service.UserMemberRightsService;
import com.medusa.gruul.user.service.service.addon.UserAddonSupporter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户会员权益service实现
 *
 * @author xiaoq
 * @description UserMemberRightsServiceImpl.java
 * @date 2022-11-09 15:24
 */
@Service
@RequiredArgsConstructor
public class UserMemberRightsServiceImpl implements UserMemberRightsService {
    private final IMemberRightsService memberRightsService;
    private final UserAddonSupporter userAddonSupporter;

    /**
     * 修改会员权益内容
     *
     * @param memberRights 会员权益DTO
     */
    @Override
    public void updateMemberRights(MemberRightsDTO memberRights) {
        MemberRights rights = memberRightsService.lambdaQuery().eq(BaseEntity::getId, memberRights.getId()).one();
        if (rights == null) {
            throw new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, "当前权益不存在");
        }
        BeanUtils.copyProperties(memberRights, rights);
        memberRightsService.updateById(rights);
    }

    /**
     * 新增会员权益内容
     *
     * @param memberRights 会员权益DTO
     */
    @Override
    public void saveMemberRights(MemberRightsDTO memberRights) {
        MemberRights rights = new MemberRights();
        BeanUtils.copyProperties(memberRights, rights);
        //设置新增权益默认值
        rights.setRightsSwitch(Boolean.FALSE);
        rights.setRightsType(RightsType.USER_DEFINED);
        rights.setId(null);
        boolean save = memberRightsService.save(rights);
        if (!save) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "会员权益新增失败");
        }
    }

    /**
     * 会员权益 启动或关闭
     *
     * @param rightsSwitch 权益开关状态
     * @param id           权益id
     */
    @Override
    public void memberRightsSwitch(Boolean rightsSwitch, Long id) {
        MemberRights rights = memberRightsService.lambdaQuery().eq(BaseEntity::getId, id).one();
        if (rights == null) {
            throw new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, "当前权益不存在");
        }
        if (!rightsSwitch) {
            //关闭会员权益 需要校验该会员权益是否被使用
            //查询免费会员权益
            Boolean inUse =memberRightsService.checkRightInUse(id);
            if (inUse) {
                throw UserError.USER_RIGHT_IN_USE_CAN_NOT_DELETE.exception();
            }
            //查询付费会员权益
             inUse = userAddonSupporter.queryMemberRightInUse(id);
            if (inUse) {
                throw UserError.USER_RIGHT_IN_USE_CAN_NOT_DELETE.exception();
            }
        }
        rights.setRightsSwitch(rightsSwitch);
        memberRightsService.updateById(rights);
    }

    /**
     * 会员权益删除
     *
     * @param ids 会员权益ids
     */
    @Override
    public void delMemberRights(Long[] ids) {
        Long count=memberRightsService.lambdaQuery()
                .in(MemberRights::getId,Arrays.asList(ids))
                .eq(MemberRights::getRightsSwitch,Boolean.TRUE)
                .count();
        if (count>0){
            throw UserError.USER_RIGHT_IS_OPEN_CAN_NOT_DELETE.exception();
        }

        boolean remove = memberRightsService.lambdaUpdate()
                .eq(MemberRights::getRightsType, RightsType.USER_DEFINED)
                .in(BaseEntity::getId, Arrays.asList(ids))
                .remove();

        if (!remove) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED_CODE, "权益删除失败,请确保权益是自定义权益");
        }
    }

    /**
     * 获取会员权益数据
     *
     * @param page 分页
     * @return Page<MemberRights>
     */
    @Override
    public Page<MemberRights> getMemberRightsList(Page<MemberRights> page) {
        Long count = memberRightsService.lambdaQuery().count();
        if (count < CommonPool.NUMBER_ONE) {
            //获取json数据转换成List<entity>
            List<MemberRights> memberRights = JSONUtil.parseArray(ResourceUtil.readStr("config/test.json", CharsetUtil.CHARSET_UTF_8)).toList(MemberRights.class);
            boolean flag = memberRightsService.saveBatch(memberRights);
            if (!flag) {
                throw new GlobalException("默认权益初始化失败");
            }
        }
        // 排序查询会员权益数据
        return memberRightsService.lambdaQuery()
                .select(
                        BaseEntity::getId,
                        MemberRights::getRightsType,
                        MemberRights::getRightsExplain,
                        MemberRights::getRightsIcon,
                        MemberRights::getRightsName,
                        MemberRights::getRightsSwitch,
                        BaseEntity::getCreateTime
                )
                .orderByAsc(MemberRights::getRightsType)
                .orderByAsc(BaseEntity::getCreateTime)
                .page(page);
    }

    /**
     * 可用会员权益获取
     *
     * @return List<MemberRights>
     */
    @Override
    public List<MemberRights> getUsableMemberRightsList() {
        // 排序查询会员权益数据
        return memberRightsService.lambdaQuery()
                .select(
                        BaseEntity::getId,
                        MemberRights::getRightsType,
                        MemberRights::getRightsName
                )
                .eq(MemberRights::getRightsSwitch, Boolean.TRUE)
                .orderByAsc(MemberRights::getRightsType)
                .orderByAsc(BaseEntity::getCreateTime)
                .list();
    }

    /**
     * 会员权益恢复默认值 by 权益类型
     *
     * @param rightsType 权益类型
     * @return 会员权益
     */
    @Override
    public MemberRights memberRightsRestoreDefault(RightsType rightsType) {
        //获取json数据转换成List<entity>
        List<MemberRights> memberRightsList = JSONUtil.parseArray(ResourceUtil.readStr("config/test.json", CharsetUtil.CHARSET_UTF_8)).toList(MemberRights.class);
        Map<RightsType, MemberRights> rightsMap = memberRightsList.stream().collect(Collectors.toMap(MemberRights::getRightsType, Function.identity()));
        return rightsMap.get(rightsType);
    }
}
