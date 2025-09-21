package com.medusa.gruul.addon.bargain.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.bargain.model.dto.BargainDTO;
import com.medusa.gruul.addon.bargain.model.dto.BargainQueryDTO;
import com.medusa.gruul.addon.bargain.model.dto.BargainShopIdDTO;
import com.medusa.gruul.addon.bargain.model.vo.BargainDetailVO;
import com.medusa.gruul.addon.bargain.model.vo.BargainInfoVO;
import com.medusa.gruul.addon.bargain.service.BargainManageService;
import com.medusa.gruul.addon.bargain.vo.BargainVo;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.overview.api.enums.ActivityConfigType;
import com.medusa.gruul.overview.api.exception.RemoteResult;
import com.medusa.gruul.overview.api.model.ActivityConfigDTO;
import com.medusa.gruul.overview.api.rpc.ActivityConfigRpcService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 砍价活动表 前端控制器
 *
 * @author WuDi
 * @since 2023-03-14
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/bargain")
public class BargainController {

    private final BargainManageService bargainService;

    private final ActivityConfigRpcService activityConfigRpcService;

    /**
     * 添加砍价活动
     *
     * @param bargainActivity 砍价活动
     */
    @Log("添加砍价活动")
    @PostMapping
    @PreAuthorize("@S.shopPerm('marketingApp:bargain')")
    public Result<Void> addBargain(@RequestBody @Valid BargainDTO bargainActivity) {
        bargainService.addBargain(bargainActivity);
        return Result.ok();
    }

    /**
     * 查询砍价活动详情
     *
     * @param shopId     店铺id
     * @param activityId 砍价活动id
     * @return 砍价活动详情
     */
    @Log("查询砍价活动详情")
    @PreAuthorize("@S.anyPerm('bargain','marketingApp:bargain')")
    @GetMapping("/{shopId}/{activityId}")
    public Result<BargainDetailVO> getBargainDetail(@PathVariable Long shopId,
                                                    @PathVariable Long activityId) {
        return Result.ok(
                bargainService.getBargainDetail(shopId, activityId)
        );
    }

    /**
     * 分页查询砍价活动
     *
     * @param bargainQuery 查询砍价活动参数
     * @return 砍价活动分页
     */
    @Log("分页查询砍价活动")
    @GetMapping
    @PreAuthorize("@S.anyPerm('bargain','marketingApp:bargain')")
    public Result<IPage<BargainInfoVO>> bargainInfoPage(BargainQueryDTO bargainQuery) {
        ISecurity.match()
                .ifAnyShopAdmin(user -> bargainQuery.setShopId(user.getShopId()));
        return Result.ok(
                bargainService.bargainInfoPage(bargainQuery)
        );
    }

    /**
     * 批量删除砍价活动 平台端  店铺端 调用的都是这个接口
     *
     * @param bargainShopId 砍价id、店铺id
     */
    @Log("批量删除砍价活动")
    @DeleteMapping
    @PreAuthorize("@S.anyPerm('bargain','marketingApp:bargain')")
    public Result<Void> deleteBatchBargain(@RequestBody @Valid @Size(min = 1) List<BargainShopIdDTO> bargainShopId) {
        bargainService.deleteBatchBargain(bargainShopId);
        return Result.ok();
    }

    /**
     * 平台端 违规下架砍价活动
     *
     * @param bargainVo 砍价活动信息
     */
    @Log("平台下架砍价活动")
    @PreAuthorize("@S.platformPerm('bargain')")
    @PutMapping("platform/sellOf")
    public Result<Void> platformSellOfBargain(@RequestBody @Valid BargainVo bargainVo) {
        bargainService.sellOfBargain(bargainVo);
        return Result.ok();
    }

    @Log("店铺下架砍价活动")
    @PreAuthorize("@S.shopPerm('marketingApp:bargain')")
    @PutMapping("shop/sellOf")
    public Result<Void> shopSellOfBargain(@RequestBody @Valid BargainVo bargainVo) {
        bargainService.shopSellOfBargain(bargainVo);
        return Result.ok();
    }

    /**
     * 查询活动违规下架的原因
     *
     * @return 违规下架的原因
     */
    @Log("砍价活动违规下架的原因")
    @PreAuthorize("@S.anyPerm('bargain','marketingApp:bargain')")
    @GetMapping("illegal/reason/{bargainId}")
    public Result<String> queryIllegalReason(@PathVariable Long bargainId) {
        String reason = bargainService.queryIllegalReason(bargainId);
        return Result.ok(reason);
    }

    /**
     * 查询满减活动的规则
     *
     * @return 满减活动规则
     */
    @Log("获取砍价活动的规则")
    @PreAuthorize("@S.platformPerm('bargain')")
    @GetMapping("query/rule")
    public Result<Object> queryBargainConfig() {
        RemoteResult<ActivityConfigDTO> remoteResult = activityConfigRpcService.queryActivityConfigByType(
                ActivityConfigType.BARGAIN_ACTIVITY_RULE);
        if (remoteResult.isSuccess() && Objects.nonNull(remoteResult.getData())) {
            return Result.ok(remoteResult.getData().getContent());
        }
        return Result.ok();

    }

    /**
     * 保存砍价活动规则
     *
     * @return 满减活动规则
     */
    @Log("保存砍价活动规则")
    @PreAuthorize("@S.platformPerm('bargain')")
    @PutMapping("save/rule")
    public Result<Boolean> saveBargainConfig(@RequestBody ActivityConfigDTO activityConfigDTO) {
        activityConfigDTO.setType(ActivityConfigType.BARGAIN_ACTIVITY_RULE);
        activityConfigRpcService.saveOrUpdateConfig(activityConfigDTO);
        return Result.ok(Boolean.TRUE);

    }


}
