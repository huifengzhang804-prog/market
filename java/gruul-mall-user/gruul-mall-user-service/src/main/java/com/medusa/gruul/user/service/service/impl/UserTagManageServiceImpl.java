package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.user.api.model.UserTagVo;
import com.medusa.gruul.user.service.model.dto.UserTagDTO;
import com.medusa.gruul.user.service.mp.entity.UserTag;
import com.medusa.gruul.user.service.mp.entity.UserTagGroup;
import com.medusa.gruul.user.service.mp.service.IUserTagGroupService;
import com.medusa.gruul.user.service.mp.service.IUserTagService;
import com.medusa.gruul.user.service.service.UserTagManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WuDi
 * date 2022/9/20
 */
@Service
@RequiredArgsConstructor
public class UserTagManageServiceImpl implements UserTagManageService {

    private final IUserTagService userTagService;

    private final IUserTagGroupService userTagGroupService;

    /**
     * 查询会员所有标签
     *
     * @param bound  是否只查询被用户绑定的标签
     * @param shopId 店铺id
     * @return 会员标签集合
     */
    @Override
    public List<UserTagVo> getUserTagList(Boolean bound) {
        Long shopId = ISecurity.userMust().getShopId();
        List<UserTag> userTagList = userTagService.lambdaQuery()
                .select(UserTag::getId, UserTag::getTagName, UserTag::getShopId)
                .apply(BooleanUtil.isTrue(bound), "EXISTS(SELECT tagGroup.id FROM t_user_tag_group AS tagGroup WHERE tagGroup.deleted = 0 AND tagGroup.user_tag_id = t_user_tag.id)")
                .eq(UserTag::getShopId, shopId)
                .list();
        if (CollUtil.isEmpty(userTagList)) {
            return List.of();
        }
        return userTagList.stream().map(userTag -> {
            UserTagVo userTagVo = new UserTagVo();
            userTagVo.setId(userTag.getId())
                    .setTagName(userTag.getTagName())
                    .setOption(Boolean.FALSE);
            return userTagVo;
        }).collect(Collectors.toList());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserTag(UserTagDTO userTags) {
        Long shopId = ISecurity.userMust().getShopId();
        List<Long> deleteTagIds = userTags.getDelUserTagIdList();
        // 删除标签
        if (CollUtil.isNotEmpty(deleteTagIds)) {
            boolean remove = userTagService.lambdaUpdate()
                    .in(UserTag::getId, deleteTagIds)
                    .eq(UserTag::getShopId, shopId)
                    .remove();
            if (remove) {
                userTagGroupService.lambdaUpdate()
                        .in(UserTagGroup::getUserTagId, deleteTagIds)
                        .eq(UserTagGroup::getShopId, shopId)
                        .remove();
            }
        }
        //选中的标签
        Set<Long> selectedTagIds = new HashSet<>();
        // 更新标签
        List<UserTagDTO.TagDTO> updateTags = userTags.getUpdateTagList();
        if (CollUtil.isNotEmpty(updateTags)) {
            //选中的标签
            selectedTagIds.addAll(updateTags.stream().filter(tag -> BooleanUtil.isTrue(tag.getOption())).map(UserTagDTO.TagDTO::getId).toList());
            //更新标签
            userTagService.updateBatchById(
                    updateTags.stream().map(
                            updateTag -> {
                                UserTag userTag = new UserTag()
                                        .setTagName(updateTag.getTagName())
                                        .setShopId(shopId);
                                userTag.setId(updateTag.getId());
                                return userTag;
                            }
                    ).toList()
            );
        }
        // 增加标签
        List<UserTagDTO.TagDTO> newTags = userTags.getAddTagList();
        if (CollUtil.isNotEmpty(newTags)) {
            Map<Boolean, List<UserTagDTO.TagDTO>> tagsMap = newTags.stream().collect(Collectors.groupingBy(tag -> BooleanUtil.isTrue(tag.getOption())));
            List<UserTag> selectedNewTags = CollUtil.emptyIfNull(
                            tagsMap.get(Boolean.TRUE))
                    .stream()
                    .map(tag ->
                            new UserTag()
                                    .setTagName(tag.getTagName())
                                    .setShopId(shopId)
                    ).toList();
            List<UserTag> unselectedNewTags = CollUtil.emptyIfNull(tagsMap.get(Boolean.FALSE)).stream().map(tag -> new UserTag().setTagName(tag.getTagName()).setShopId(shopId)).toList();
            List<UserTag> saveTags = new ArrayList<>(selectedNewTags);
            saveTags.addAll(unselectedNewTags);
            userTagService.saveBatch(saveTags);
            if (CollUtil.isNotEmpty(selectedNewTags)) {
                selectedTagIds.addAll(selectedNewTags.stream().map(UserTag::getId).collect(Collectors.toSet()));
            }
        }
        Set<Long> userIds = userTags.getUserIdList();
        //删除之前的 用户标签
        userTagGroupService.lambdaUpdate()
                .in(UserTagGroup::getUserId, userIds)
                .eq(UserTagGroup::getShopId, shopId)
                .remove();
        List<UserTagGroup> userTagGroups = userIds.stream().flatMap(
                userId -> selectedTagIds
                        .stream()
                        .map(tagId ->
                                new UserTagGroup()
                                        .setUserId(userId)
                                        .setUserTagId(tagId)
                                        .setShopId(shopId)
                        )
        ).toList();
        //保存新的关系
        userTagGroupService.saveBatch(userTagGroups);

    }


}
