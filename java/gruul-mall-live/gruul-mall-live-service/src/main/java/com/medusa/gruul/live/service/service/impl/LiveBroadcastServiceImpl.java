package com.medusa.gruul.live.service.service.impl;

import cn.binarywang.wx.miniapp.api.*;
import cn.binarywang.wx.miniapp.api.impl.WxMaLiveGoodsServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaLiveMemberServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaLiveServiceImpl;
import cn.binarywang.wx.miniapp.bean.live.*;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductUpdateStatusDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.live.api.constant.LiveConstant;
import com.medusa.gruul.live.api.entity.LiveGoodsExamine;
import com.medusa.gruul.live.api.entity.LiveMember;
import com.medusa.gruul.live.api.entity.LiveRoom;
import com.medusa.gruul.live.api.entity.LiveRoomGoods;
import com.medusa.gruul.live.api.enums.AuditStatus;
import com.medusa.gruul.live.api.enums.RoomStatus;
import com.medusa.gruul.live.api.enums.WechatErrorCode;
import com.medusa.gruul.live.service.model.dto.*;
import com.medusa.gruul.live.service.mp.service.LiveGoodsExamineService;
import com.medusa.gruul.live.service.mp.service.LiveMemberService;
import com.medusa.gruul.live.service.mp.service.LiveRoomGoodsService;
import com.medusa.gruul.live.service.mp.service.LiveRoomService;
import com.medusa.gruul.live.service.service.LiveBroadcastService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author miskw
 * @date 2022/11/9
 */
@Service
@DubboService
@RequiredArgsConstructor
public class LiveBroadcastServiceImpl implements LiveBroadcastService {
    private final WxMaService wxMaService;
    private final LiveGoodsExamineService liveGoodsExamineService;
    private final LiveMemberService liveMemberService;
    private final LiveRoomService liveRoomService;
    private final GoodsRpcService goodsRpcService;
    private final LiveRoomGoodsService liveRoomGoodsService;
    private final ShopRpcService shopRpcService;
    @Value("${gruul.wechat.app-live-url:''}")
    private String goodsUrl;

    /**
     * 上传素材
     *
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadSourceMaterial(MultipartFile file,String suffix) {
        WxMaMediaService wxMaMediaService = wxMaService.getMediaService();
        WxMediaUploadResult wxMediaUploadResult = null;
        try {
            wxMediaUploadResult = wxMaMediaService.uploadMedia("image", suffix, file.getInputStream());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
        if (StrUtil.isBlank(wxMediaUploadResult.getMediaId())) {
            throw new GlobalException(SystemCode.WX_PLATEFROM_EXCEPTION, "微信上传素材失败！");
        }

        return wxMediaUploadResult.getMediaId();
    }

    /**
     * 添加商品审核
     *
     * @param dto
     * @return
     */
    @Override
    public void goodsAdd(AddGoodsDto dto) {
        //校验商品名称是否符合3个中文或者6个字符
        Boolean status = checkParam(dto.getProductName());
        if (!status) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "名称长度非法,最少3个汉字或者6个字符、最大14个汉字！");
        }

        Long shopId = ISecurity.userMust().getShopId();
        dto.setShopId(shopId);
        dto.setUrl(StrUtil.format(goodsUrl, dto.getProductId(), dto.getShopId()));
        WxMaLiveGoodsService wxMaLiveGoodsService = wxMaService.getLiveGoodsService();
        //封装审核商品所需参数
        WxMaLiveGoodInfo wxMaLiveGoodInfo = getWxMaLiveGoodInfo(dto);
        WxMaLiveResult wxMaLiveResult = null;

        try {
            wxMaLiveResult = wxMaLiveGoodsService.addGoods(wxMaLiveGoodInfo);
        } catch (WxErrorException e) {
            if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(e.getError().getErrorCode()))) {
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.getDescribe(e.getError().getErrorCode()));
            }
            throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), e.getMessage());
        }
        Integer goodsId = wxMaLiveResult.getGoodsId();
        if (goodsId == null) {
            throw new GlobalException(SystemCode.WX_PLATEFROM_EXCEPTION, "微信添加商品校验失败!");
        }
        Long auditId = wxMaLiveResult.getAuditId();

        //封装添加直播商品审核数据实体
        ShopInfoVO shopInfoByShopId = shopRpcService.getShopInfoByShopId(shopId);
        LiveGoodsExamine liveGoodsExamine = getLiveGoodsExamineBean(dto, goodsId, auditId, shopId);
        liveGoodsExamine.setShopName(shopInfoByShopId.getName());
        boolean flag = liveGoodsExamineService.save(liveGoodsExamine);
        if (!flag) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "添加商品失败");
        }
    }

    private Boolean checkParam(String productName) {
        String pattern = "[\\u4e00-\\u9fa5]+";
        List<String> all = ReUtil.findAll(pattern, productName, 0);
        int count = 0;
        if (CollUtil.isNotEmpty(all)) {
            for (String s : all) {
                count += s.length();
            }
            int otherChinese = productName.length() - count;
            count = count * CommonPool.NUMBER_TWO + otherChinese;
            if (count >= CommonPool.NUMBER_SIX) {
                return Boolean.TRUE;
            }
        }
        return productName.length() < CommonPool.NUMBER_SIX ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * 设置成员角色
     *
     * @param roleDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(RoleDto roleDto) {
        Long shopId = ISecurity.userMust().getShopId();
        WxMaLiveMemberService wxMaLiveMemberService = new WxMaLiveMemberServiceImpl(wxMaService);
        String resultStr = "";
        try {
            resultStr = wxMaLiveMemberService.addRole(roleDto.getUsername(), roleDto.getRole().getCode());
        } catch (WxErrorException e) {
            int errorCode = e.getError().getErrorCode();
            if (errorCode == WechatErrorCode.REAL_NAME_AUTHENTICATION.getCode()) {
                throw new GlobalException(errorCode, WechatErrorCode.REAL_NAME_AUTHENTICATION.getDescribe());
            }
            if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(errorCode))) {
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.getDescribe(errorCode));
            }
            throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), e.getMessage());
        }

        JSONObject jsonObject = JSONUtil.parseObj(resultStr);
        Boolean flag = (Boolean) jsonObject.get("success");
        if (!flag) {
            throw new GlobalException(SystemCode.WX_PLATEFROM_EXCEPTION, "微信添加主播成员失败!");
        }
        try {
            //保存微信号前判断是否已经存在
            Long count = liveMemberService.lambdaQuery()
                    .eq(LiveMember::getWechatNumber, roleDto.getUsername())
                    .eq(LiveMember::getShopId, shopId)
                    .eq(LiveMember::getDeleted, CommonPool.NUMBER_ZERO)
                    .count();
            if (count > CommonPool.NUMBER_ZERO) {
                throw new GlobalException(WechatErrorCode.CREATE_ANCHOR_REPEAT.getCode(),
                        WechatErrorCode.CREATE_ANCHOR_REPEAT.getDescribe());
            }
            //添加直播成员信息
            JsonArray jsonElements = wxMaLiveMemberService.listByRole(roleDto.getRole().getCode(), 0, 1, roleDto.getUsername());
            LiveMember liveMember = getLiveMemberBean(jsonElements, shopId);
            liveMember.setWechatNumber(roleDto.getUsername());
            liveMember.setRole(roleDto.getRole());
            ShopInfoVO shopInfoByShopId = shopRpcService.getShopInfoByShopId(shopId);
            liveMember.setShopName(shopInfoByShopId.getName());
            boolean liveMemberFlag = liveMemberService.save(liveMember);
            if (!liveMemberFlag) {
                throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "添加直播成员信息失败!");
            }
        } catch (WxErrorException e) {
            throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), e.getMessage());
        }

    }

    /**
     * 直播间成员列表
     *
     * @param liveMemberDto
     * @return
     */
    @Override
    public IPage<LiveMember> getLiveMember(LiveMemberDto liveMemberDto) {
        return liveMemberService.getLiveMember(liveMemberDto);
    }

    /**
     * 创建直播间
     *
     * @param wxMaLiveRoomDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRoom(WxMaLiveRoomDto wxMaLiveRoomDto) {
        try {
            Boolean roomName = checkParam(wxMaLiveRoomDto.getName());
            if (!roomName) {

                throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "名称长度非法,最少3个汉字或者6个字符、最大17个汉字！");
            }

            Long shopId = ISecurity.userMust().getShopId();
            WxMaLiveService wxMaLiveService = new WxMaLiveServiceImpl(wxMaService);
            WxMaLiveRoomInfo wxMaLiveRoomInfo = new WxMaLiveRoomInfo();
            BeanUtil.copyProperties(wxMaLiveRoomDto, wxMaLiveRoomInfo);
            WxMaCreateRoomResult roomResult = wxMaLiveService.createRoom(wxMaLiveRoomInfo);
            if (roomResult.getQrcodeUrl() != null) {
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.CURRENT_ANCHOR_WECHAT_NUMBER_NON_REAL_NAME.getDescribe());
            }
            Integer roomId = roomResult.getRoomId();
            //判断直播类型，如果是推流，则获取推流地址
            String pushUrl = "";
            if (wxMaLiveRoomDto.getType() == 1) {
                pushUrl = this.getPushUrl(roomId);
            }
            //添加直播间
            ShopInfoVO shopInfoByShopId = shopRpcService.getShopInfoByShopId(shopId);
            liveRoomService.createRoom(roomId, shopId, wxMaLiveRoomDto, pushUrl, shopInfoByShopId);
        } catch (WxErrorException e) {
            if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(e.getError().getErrorCode()))) {
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.getDescribe(e.getError().getErrorCode()));
            }
            throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), e.getMessage());
        }
    }

    /**
     * 获取直播间列表
     *
     * @param liveInfoDto
     * @return
     */
    @Override
    public IPage<LiveRoom> getLiveList(LiveInfoDto liveInfoDto) {
        return liveRoomService.getLiveList(liveInfoDto);
    }

    /**
     * 获取直播间详情
     *
     * @param id
     * @return
     */
    @Override
    public LiveRoom getLiveInfo(String id) {
        return liveRoomService.getLiveInfo(Long.valueOf(id));
    }

    /**
     * 获取直播商品
     *
     * @param getGoodsDto
     * @return
     */
    @Override
    public IPage<LiveGoodsExamine> getGoods(GetGoodsDto getGoodsDto) {
        return liveGoodsExamineService.getGoods(getGoodsDto);
    }

    /**
     * 撤回商品审核
     *
     * @param goodsId
     * @param auditId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void goodsResetAudit(Integer goodsId, Long auditId) {
        WxMaLiveGoodsService wxMaLiveGoodsService = new WxMaLiveGoodsServiceImpl(wxMaService);
        try {
            boolean flag = wxMaLiveGoodsService.resetAudit(auditId.intValue(), goodsId);
            if (!flag) {
                throw new GlobalException(SystemCode.WX_PLATEFROM_EXCEPTION, "微信撤回商品审核异常");
            }
            liveGoodsExamineService.updateByGoodsStatus(goodsId, AuditStatus.UNAPPROVED);
        } catch (WxErrorException e) {
            if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(e.getError().getErrorCode()))) {
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.getDescribe(e.getError().getErrorCode()));
            }
            throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), e.getMessage());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 重新提交商品审核
     *
     * @param goodsId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resubmitAudit(Integer goodsId) {
        Long shopId = ISecurity.userMust().getShopId();
        LiveGoodsExamine liveGoodsExamine = liveGoodsExamineService.lambdaQuery().eq(LiveGoodsExamine::getGoodsId, Long.valueOf(goodsId)).one();
        if (liveGoodsExamine == null) {
            throw new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, "当前直播商品不存在!");
        }
        Long productId = liveGoodsExamine.getProductId();
        Product productInfo = goodsRpcService.getProductInfo(shopId, productId);
        if (productInfo == null || productInfo.getStatus().getStatus() != 1) {
            throw new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, "当前直播商品关联的商品已下架或已被删除!");
        }
        WxMaLiveGoodsService wxMaLiveGoodsService = new WxMaLiveGoodsServiceImpl(wxMaService);
        try {
            String auditGoods = wxMaLiveGoodsService.auditGoods(goodsId);
            if (StrUtil.isEmpty(auditGoods)) {
                throw new GlobalException(SystemCode.WX_PLATEFROM_EXCEPTION, "微信重新提交商品审核失败!");
            }
            liveGoodsExamineService.updateByGoodsStatus(goodsId, AuditStatus.UNDER_REVIEW);

        } catch (WxErrorException e) {
            if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(e.getError().getErrorCode()))) {
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.getDescribe(e.getError().getErrorCode()));
            }
            throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), e.getMessage());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 批量删除直播间
     *
     * @param roomIds
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoom(Long[] roomIds) {
        WxMaLiveService wxMaLiveService = new WxMaLiveServiceImpl(wxMaService);
        for (Long roomId : roomIds) {

            try {
                //删除直播间
                boolean flag = wxMaLiveService.deleteRoom(roomId.intValue());
                if (!flag) {
                    throw new GlobalException("微信删除直播间失败!");
                }
            } catch (WxErrorException e) {
                if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(e.getError().getErrorCode()))) {
                    throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.getDescribe(e.getError().getErrorCode()));
                }
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), e.getMessage());
            } catch (Exception e) {
                throw new GlobalException(e.getMessage());
            }
        }
        boolean flag = liveRoomService.lambdaUpdate().in(LiveRoom::getWechatRoomId, roomIds).remove();
        if (!flag) {
            throw new GlobalException(SystemCode.DATA_DELETE_FAILED_CODE, "批量删除直播间失败");
        }
    }

    /**
     * 获取直播间商品
     *
     * @param dto
     * @return
     */
    @Override
    public IPage<LiveGoodsExamine> getRoomGoods(BroadcastRoomGoodsDto dto) {
        //直播商品Ids
        List<LiveRoomGoods> liveRoomGoods = liveRoomGoodsService
                .lambdaQuery()
                .eq(LiveRoomGoods::getLiveRoomId, dto.getRoomId()).list();
        if (CollUtil.isEmpty(liveRoomGoods)) {
            return new Page<>(dto.getCurrent(), dto.getSize());
        }
        List<Long> liveGoodsExamineIds = liveRoomGoods.stream().map(LiveRoomGoods::getLiveGoodsExamineId).collect(Collectors.toList());

        IPage<LiveGoodsExamine> page = new Page<>(dto.getCurrent(), dto.getSize());
        liveGoodsExamineService.lambdaQuery()
                .like(StrUtil.isNotBlank(dto.getKeywords()), LiveGoodsExamine::getProductName, dto.getKeywords())
                .in(LiveGoodsExamine::getGoodsId, liveGoodsExamineIds).page(page);
        return page;
    }


    /**
     * 直播间导入商品
     *
     * @param addLiveGoodsDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoomGoods(AddLiveGoodsDto addLiveGoodsDto) {
        Long roomId = addLiveGoodsDto.getRoomId();
        LiveRoom one = this.liveRoomService.lambdaQuery().eq(LiveRoom::getWechatRoomId, roomId).one();
        if (one == null || one.getStatus() == RoomStatus.CLOSED) {
            throw new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, "当前直播间不存在或者当前直播间已关闭不能添加商品！");
        }
        WxMaLiveService wxMaLiveService = new WxMaLiveServiceImpl(wxMaService);
        List<Integer> list = addLiveGoodsDto.getGoodsIds().stream().map(Long::intValue).collect(Collectors.toList());
        try {
            boolean flag = wxMaLiveService.addGoodsToRoom(addLiveGoodsDto.getRoomId().intValue(), list);
            if (!flag) {
                throw new GlobalException(SystemCode.WX_PLATEFROM_EXCEPTION, "微信直播间导入商品失败!");
            }
            //直播间商品，数据添加到数据库
            liveRoomGoodsService.addRoomGoods(addLiveGoodsDto);
        } catch (WxErrorException e) {
            if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(e.getError().getErrorCode()))) {
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.getDescribe(e.getError().getErrorCode()));
            }
            throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), e.getMessage());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 修改直播商品
     *
     * @param dto
     * @return
     */
    @Override
    public void goodsUpdate(AddGoodsDto dto) {
        dto.setUrl(StrUtil.format(goodsUrl, dto.getProductId(), ISecurity.userMust().getShopId()));
        WxMaLiveGoodsService wxMaLiveGoodsService = new WxMaLiveGoodsServiceImpl(wxMaService);
        WxMaLiveGoodInfo wxMaLiveGoodInfo = new WxMaLiveGoodInfo();
        BeanUtil.copyProperties(dto, wxMaLiveGoodInfo);
        try {
            boolean flag = wxMaLiveGoodsService.updateGoods(wxMaLiveGoodInfo);
            if (!flag) {
                throw new GlobalException(SystemCode.WX_PLATEFROM_EXCEPTION, "微信修改直播商品失败!");
            }
            liveGoodsExamineService.goodsUpdate(dto);
            resubmitAudit(dto.getGoodsId().intValue());
        } catch (WxErrorException e) {
            if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(e.getError().getErrorCode()))) {
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.getDescribe(e.getError().getErrorCode()));
            }
            throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), e.getMessage());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }

    }

    /**
     * 批量删除直播商品
     *
     * @param goodsIds
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGoodsInfos(Long[] goodsIds) {
        WxMaLiveGoodsService wxMaLiveGoodsService = new WxMaLiveGoodsServiceImpl(wxMaService);
        try {
            for (Long goodsId : goodsIds) {

                boolean flag = wxMaLiveGoodsService.deleteGoods(goodsId.intValue());
                if (!flag) {
                    throw new GlobalException(SystemCode.WX_PLATEFROM_EXCEPTION, "微信删除直播商品失败！");
                }
            }
            //同步批量删除直播商品
            boolean flag = liveGoodsExamineService.lambdaUpdate().in(LiveGoodsExamine::getGoodsId, Arrays.asList(goodsIds)).remove();
            if (!flag) {
                throw new GlobalException(SystemCode.DATA_DELETE_FAILED_CODE, "同步批量删除直播商品失败!");
            }

        } catch (WxErrorException e) {
            if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(e.getError().getErrorCode()))) {
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.getDescribe(e.getError().getErrorCode()));
            }
            throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), e.getMessage());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 分享直播间
     *
     * @param roomId
     * @return
     */
    @Override
    public String shareLiveRoom(Long roomId) {
        WxMaLiveService wxMaLiveService = new WxMaLiveServiceImpl(wxMaService);
        try {
            WxMaLiveSharedCode sharedCode = wxMaLiveService.getSharedCode(roomId.intValue(), null);
            return sharedCode.getCdnUrl();
        } catch (WxErrorException e) {
            if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(e.getError().getErrorCode()))) {
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.getDescribe(e.getError().getErrorCode()));
            }
            throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), e.getMessage());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }

    }

    /**
     * 批量删除主播
     *
     * @param liveUserIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLiveUser(Long[] liveUserIds) {
        List<LiveMember> list = liveMemberService.lambdaQuery().in(LiveMember::getId, Arrays.asList(liveUserIds)).list();

        WxMaLiveMemberService wxMaLiveMemberService = new WxMaLiveMemberServiceImpl(wxMaService);
        for (LiveMember liveMember : list) {
            try {
                String resultJson = wxMaLiveMemberService.deleteRole(liveMember.getWechatNumber(), liveMember.getRole().getCode());
                JSONObject jsonObject = JSONUtil.parseObj(resultJson);
                Boolean flag = (Boolean) jsonObject.get("success");
                if (!flag) {
                    throw new GlobalException(SystemCode.WX_PLATEFROM_EXCEPTION, "微信删除用户角色失败！");
                }
            } catch (WxErrorException e) {
                if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(e.getError().getErrorCode()))) {
                    throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.getDescribe(e.getError().getErrorCode()));
                }
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), e.getMessage());
            } catch (Exception e) {
                throw new GlobalException(e.getMessage());
            }
        }

        boolean flag = liveMemberService.lambdaUpdate().in(LiveMember::getId, Arrays.asList(liveUserIds)).remove();
        if (!flag) {
            throw new GlobalException(SystemCode.DATA_DELETE_FAILED_CODE, "批量删除用户角色失败！");
        }
    }

    /**
     * 获取直播商品详情
     *
     * @param goodsId
     * @return
     */
    @Override
    public LiveGoodsExamine getGoodsInfo(Long goodsId) {
        LiveGoodsExamine one = liveGoodsExamineService.lambdaQuery()
                .eq(LiveGoodsExamine::getGoodsId, goodsId)
                .one();
        return one;
    }

    /**
     * 平台下架商品 关联直播商品违规下架
     *
     * @param dtoList 商品修改状态dto
     */
    @Override
    public void liveChangeQueue(List<ProductUpdateStatusDTO> dtoList) {
        dtoList = dtoList
                .stream()
                .filter(item -> item.getProductStatus().getStatus() == ProductStatus.PLATFORM_SELL_OFF.getStatus())
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(dtoList)) {
            return;
        }
        dtoList.forEach(productUpdateStatusDTO -> {
            List<LiveGoodsExamine> liveGoodsExamines = liveGoodsExamineService
                    .lambdaQuery()
                    .eq(productUpdateStatusDTO.getShopId() != null, LiveGoodsExamine::getShopId, productUpdateStatusDTO.getShopId())
                    .in(CollUtil.isNotEmpty(productUpdateStatusDTO.getProductIds()), LiveGoodsExamine::getProductId, productUpdateStatusDTO.getProductIds())
                    .list();
            if (CollUtil.isEmpty(liveGoodsExamines)) {
                return;
            }

            List<Integer> goodsIds = liveGoodsExamines
                    .stream()
                    .map(item -> item.getGoodsId().intValue())
                    .collect(Collectors.toList());
            WxMaLiveGoodsService wxMaLiveGoodsService = wxMaService.getLiveGoodsService();
            WxMaLiveResult goodsWareHouse = null;
            try {
                goodsWareHouse = wxMaLiveGoodsService.getGoodsWareHouse(goodsIds);
                //审核中的直播商品
                List<Long> auditGoodsIds = goodsWareHouse
                        .getGoods()
                        .stream()
                        .filter(item -> item.getAuditStatus() == AuditStatus.UNDER_REVIEW.getCode())
                        .map(item -> item.getGoodsId().longValue())
                        .toList();

                //撤回审核中商品
                liveGoodsExamines
                        .stream()
                        .filter(liveGoodsExamine -> auditGoodsIds.contains(liveGoodsExamine.getGoodsId()))
                        .forEach(liveGoodsExamine -> goodsResetAudit(liveGoodsExamine.getGoodsId().intValue(), liveGoodsExamine.getAuditId()));

            } catch (WxErrorException e) {
                if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(e.getError().getErrorCode()))) {
                    throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.getDescribe(e.getError().getErrorCode()));
                }
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), e.getMessage());
            } catch (Exception e) {
                throw new GlobalException(e.getMessage());
            }

            boolean flag = liveGoodsExamineService.lambdaUpdate()
                    .in(LiveGoodsExamine::getGoodsId, goodsIds)
                    .set(LiveGoodsExamine::getAuditStatus, AuditStatus.VIOLATION__OFF_SHELF)
                    .update();
            if (!flag) {
                throw new GlobalException(SystemCode.DATA_UPDATE_FAILED_CODE, "直播商品违规下架失败!");
            }
        });

    }

    /**
     * 添加直播成员信息
     *
     * @param jsonElements
     * @param shopId
     * @return
     */
    private LiveMember getLiveMemberBean(JsonArray jsonElements, Long shopId) {
        JsonElement jsonElement = jsonElements.get(0);
        JSONObject jsonObject = JSONUtil.parseObj(jsonElement.toString());
        String nickname = jsonObject.get(LiveConstant.JSON_NICKNAME).toString();
        String headingImg = jsonObject.get(LiveConstant.JSON_HEADING_IMG).toString();
        LiveMember liveMember = new LiveMember();
        liveMember.setUserName(nickname);
        liveMember.setAvatarUrl(headingImg);
        liveMember.setShopId(shopId);
        return liveMember;
    }

    /**
     * 封装添加直播商品审核数据实体
     *
     * @param dto
     * @param goodsId
     * @return
     */
    private LiveGoodsExamine getLiveGoodsExamineBean(AddGoodsDto dto, Integer goodsId, Long auditId, Long shopId) {
        LiveGoodsExamine liveGoodsExamine = new LiveGoodsExamine();
        liveGoodsExamine.setProductId(dto.getProductId())
                .setProductName(dto.getProductName())
                .setShopId(shopId)
                .setAuditId(auditId)
                .setOssImgUrl(dto.getOssImgUrl())
                .setGoodsId(Long.valueOf(goodsId))
                .setPriceType(dto.getPriceType().intValue())
                .setUrl(dto.getUrl())
                .setCoverImgUrl(dto.getCoverImgUrl())
                .setVerifyTime(LocalDateTime.now())
                .setAuditStatus(AuditStatus.UNDER_REVIEW);
        switch (dto.getPriceType().intValue()) {
            case 1:
                liveGoodsExamine.setPrice(dto.getPrice());
                break;
            case 2:
            case 3:
                liveGoodsExamine.setPrice(dto.getPrice());
                liveGoodsExamine.setPrice2(dto.getPrice2());
                break;
            default:
                break;
        }


        return liveGoodsExamine;
    }

    /**
     * 封装审核商品所需参数
     *
     * @param dto
     * @return
     */
    private WxMaLiveGoodInfo getWxMaLiveGoodInfo(AddGoodsDto dto) {
        WxMaLiveGoodInfo wxMaLiveGoodInfo = new WxMaLiveGoodInfo();
        wxMaLiveGoodInfo.setCoverImgUrl(dto.getCoverImgUrl());
        wxMaLiveGoodInfo.setName(dto.getProductName());
        //处理商品URL
        wxMaLiveGoodInfo.setUrl(dto.getUrl());
        wxMaLiveGoodInfo.setPriceType(dto.getPriceType().intValue());
        BigDecimal priceBigDecimal = BigDecimal.valueOf(dto.getPrice());
        BigDecimal conversion = BigDecimal.valueOf(10000);
        BigDecimal divide = priceBigDecimal.divide(conversion, 3, RoundingMode.DOWN);
        switch (dto.getPriceType().intValue()) {
            case 1:
                wxMaLiveGoodInfo.setPrice(divide);
                break;
            case 2:
            case 3:
                BigDecimal rightBigDecimal = BigDecimal.valueOf(dto.getPrice2());
                wxMaLiveGoodInfo.setPrice(divide);
                wxMaLiveGoodInfo.setPrice2(rightBigDecimal.divide(conversion, 3, RoundingMode.DOWN));
                break;
            default:
                break;
        }
        return wxMaLiveGoodInfo;
    }

    /**
     * 获取微信推流地址
     *
     * @return
     */
    private String getPushUrl(Integer roomId) {
        WxMaLiveService wxMaLiveService = new WxMaLiveServiceImpl(wxMaService);
        try {
            return wxMaLiveService.getPushUrl(roomId);
        } catch (WxErrorException e) {
            if (StrUtil.isNotBlank(WechatErrorCode.getDescribe(e.getError().getErrorCode()))) {
                throw new GlobalException(WechatErrorCode.PUBLIC_CODE.getCode(), WechatErrorCode.getDescribe(e.getError().getErrorCode()));
            }
            throw new GlobalException(e.getMessage());
        }

    }


}
