package com.medusa.gruul.addon.live.function.handler.ban;

import com.medusa.gruul.addon.live.enums.AnchorStatus;
import com.medusa.gruul.addon.live.enums.LiveRoomStatus;
import com.medusa.gruul.addon.live.enums.ProhibitedType;
import com.medusa.gruul.addon.live.model.UpdateAnchorDTO;
import com.medusa.gruul.addon.live.mp.entity.Anchor;
import com.medusa.gruul.addon.live.mp.entity.BaseLive;
import com.medusa.gruul.addon.live.mp.entity.Prohibited;
import com.medusa.gruul.addon.live.mp.service.AnchorService;
import com.medusa.gruul.addon.live.mp.service.BaseLiveService;
import com.medusa.gruul.addon.live.mp.service.ProhibitedService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author miskw
 * @date 2023/6/15
 * @describe 描述
 */
@Component
@RequiredArgsConstructor
@ProhibitedTypeAnnotation(ProhibitedType.ANCHOR)
public class BanAnchor extends AbstractProhibitedTypeHandler {
    private final AnchorService anchorService;
    private final UaaRpcService uaaRpcService;
    private final ProhibitedService prohibitedService;
    private final BaseLiveService baseLiveService;

    @Override
    public List<BaseLive> handler(UpdateAnchorDTO updateAnchorDTO) {
        Anchor anchor = Option.of(anchorService.getById(updateAnchorDTO.getSourceId()))
                .getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, SystemCode.DATA_NOT_EXIST.getMsg()));
        if (updateAnchorDTO.getIsEnable() && anchor.getStatus() != AnchorStatus.VIOLATION) {
            throw new GlobalException( SystemCode.REQ_REJECT_CODE, "平台只能恢复违规禁播的主播");
        } else if (!updateAnchorDTO.getIsEnable() && anchor.getStatus() == AnchorStatus.VIOLATION) {
            return new ArrayList<>();
        }
        Long userId = ISecurity.userMust().getId();
        Option<UserInfoVO> userInfoVOS = uaaRpcService.getUserDataByUserId(userId);
        UserInfoVO userInfoVO = userInfoVOS.getOrElseThrow(() -> new GlobalException(SystemCode.REQ_REJECT_CODE, "质检员用户信息为空"));

        if (updateAnchorDTO.getIsEnable()) {
            boolean remove = prohibitedService.lambdaUpdate()
                    .eq(Prohibited::getSourceId, anchor.getId())
                    .eq(Prohibited::getType, updateAnchorDTO.getType())
                    .eq(Prohibited::getShopId, anchor.getShopId())
                    .remove();
            if (!remove) {
                throw new GlobalException(SystemCode.DATA_DELETE_FAILED_CODE, SystemCode.DATA_DELETE_FAILED.getMsg());
            }
            anchor.setStatus(AnchorStatus.NORMAL);
        } else {
            Prohibited prohibitedAnchor = new Prohibited()
                    .setQualityInspector(userInfoVO.getNickname())
                    .setType(ProhibitedType.ANCHOR)
                    .setShopId(anchor.getShopId())
                    .setSourceId(updateAnchorDTO.getSourceId())
                    .setReason(updateAnchorDTO.getReason())
                    .setRelevantEvidence(updateAnchorDTO.getRelevantEvidence())
                    .setCategoryTypes(updateAnchorDTO.getCategoryTypes());
            if (!prohibitedService.save(prohibitedAnchor)) {
                throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, SystemCode.DATA_ADD_FAILED.getMsg());
            }
            anchor.setStatus(AnchorStatus.VIOLATION);
        }
        if (!anchorService.updateById(anchor)) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED_CODE, SystemCode.DATA_UPDATE_FAILED.getMsg());
        }

        if (updateAnchorDTO.getIsEnable()){
            return new ArrayList<>();
        }

        return getBaseLives(anchor);
    }


    /**
     * 获取未开播与进行中的直播间
     *
     * @param anchor 主播
     * @return 直播间列表
     */
    private List<BaseLive> getBaseLives(Anchor anchor) {
        return baseLiveService.lambdaQuery()
                .eq(BaseLive::getAnchorId, anchor.getId())
                .and(
                        qw -> qw.eq(BaseLive::getStatus, LiveRoomStatus.NOT_STARTED)
                                .or(orQw -> orQw.eq(BaseLive::getStatus, LiveRoomStatus.PROCESSING)))
                .list();
    }
}
