package com.medusa.gruul.carrier.pigeon.service.modules.addon;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import com.medusa.gruul.common.addon.model.constant.AddonConst;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 插件路由数据查询控制器
 *
 * @author 张治保
 * date 2022/3/1
 */
@RestController
@RequestMapping("/system/addon")
@RequiredArgsConstructor
public class AddonRouterController {


    /**
     * 根据插件名称查询插件是否存在
     *
     * @param name 插件名称
     * @return boolean 是否存在
     */
    @GetMapping("/{name}/exist")
    public Result<Boolean> hasAddon(@PathVariable String name) {
        return Result.ok(
                BooleanUtil.isTrue(
                        RedisUtil.getRedisTemplate().opsForSet().isMember(AddonConst.ADDON_PROVIDER_REGISTER, name)
                )
        );
    }

    /**
     * 查询已注册的插件集合
     *
     * @return 已注册的插件集合
     */
    @GetMapping("/addons")
    public Result<Set<String>> addons() {
        return Result.ok(
                CollUtil.emptyIfNull(RedisUtil.getCacheSet(AddonConst.ADDON_PROVIDER_REGISTER))
        );
    }

}
