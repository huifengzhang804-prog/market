package com.medusa.gruul.live.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.goods.api.model.dto.ProductUpdateStatusDTO;
import com.medusa.gruul.live.api.entity.LiveGoodsExamine;
import com.medusa.gruul.live.api.entity.LiveMember;
import com.medusa.gruul.live.api.entity.LiveRoom;
import com.medusa.gruul.live.service.model.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author miskw
 * @date 2022/11/9
 */
public interface LiveBroadcastService {

    /**
     * 上传素材
     * @param file
     * @return
     */
    String uploadSourceMaterial(MultipartFile file,String suffix);

    /**
     * 添加商品审核
     * @param dto
     * @return
     */
    void goodsAdd(AddGoodsDto dto);

    /**
     * 设置成员角色
     * @param roleDto
     * @return
     */
    void addRole(RoleDto roleDto);

    /**
     * 直播间成员列表
     * @param liveMemberDto
     * @return
     */
    IPage<LiveMember> getLiveMember(LiveMemberDto liveMemberDto);

    /**
     * 创建直播间
     * @param wxMaLiveRoomDto
     * @return
     */
    void createRoom(WxMaLiveRoomDto wxMaLiveRoomDto);

    /**
     * 直播间列表
     * @param liveInfoDto
     * @return
     */
    IPage<LiveRoom> getLiveList(LiveInfoDto liveInfoDto);

    /**
     * 获取直播间详情
     * @param id
     * @return
     */
    LiveRoom getLiveInfo(String id);

    /**
     * 获取直播商品
     * @param getGoodsDto
     * @return
     */
    IPage<LiveGoodsExamine> getGoods(GetGoodsDto getGoodsDto);

    /**
     * 撤回商品审核
     * @param goodsId
     * @param auditId
     * @return
     */
    void goodsResetAudit(Integer goodsId, Long auditId);

    /**
     *重新提交商品审核
     * @param goodsId
     * @return
     */
    void resubmitAudit(Integer goodsId);

    /**
     * 批量删除直播间
     * @param roomIds
     * @return
     */
    void deleteRoom(Long[] roomIds);

    /**
     * 获取直播间商品
     * @param dto
     * @return
     */
    IPage<LiveGoodsExamine> getRoomGoods(BroadcastRoomGoodsDto dto);


    /**
     * 直播间导入商品
     * @param addLiveGoodsDto
     * @return
     */
    void addRoomGoods(AddLiveGoodsDto addLiveGoodsDto);

    /**
     * 修改直播商品
     * @param dto
     * @return
     */
    void goodsUpdate(AddGoodsDto dto);

    /**
     * 批量删除直播商品
     * @param goodsIds
     * @return
     */
    void deleteGoodsInfos(Long[] goodsIds);

    /**
     * 分享直播间
     * @param roomId
     * @return
     */
    String shareLiveRoom(Long roomId);


    /**
     * 批量删除主播
     * @param liveUserIds
     */
    void deleteLiveUser(Long[] liveUserIds);

    /**
     * 获取直播商品详情
     * @param goodsId
     * @return
     */
    LiveGoodsExamine getGoodsInfo(Long goodsId);

    /**
     * 平台下架商品 关联直播商品违规下架
     * @param dtoList  商品修改状态dto
     */
    void liveChangeQueue(List<ProductUpdateStatusDTO> dtoList);
}
