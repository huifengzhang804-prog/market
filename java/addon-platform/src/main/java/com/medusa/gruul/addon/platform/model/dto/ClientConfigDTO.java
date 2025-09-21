package com.medusa.gruul.addon.platform.model.dto;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.platform.model.enums.ClientConfigType;
import com.medusa.gruul.addon.platform.mp.entity.PlatformClientConfig;
import com.medusa.gruul.addon.platform.mp.service.IPlatformClientConfigService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author 张治保
 * date 2022/4/25
 */
@Getter
@Setter
@ToString
public class ClientConfigDTO {

    /**
     * 0 平台 1商家 2小程序 3.APP 4.h5
     */
    @NotNull
    private ClientConfigType type;

    /**
     * 登录logo
     */
    private String loginLogo;

    /**
     * 登录背景
     */
    private String loginBackground;

    /**
     * 版权声明
     */
    @NotBlank
    private String copyright;

    /**
     * 标题logo
     */
    private String titleLogo;

    /**
     * 底部logo
     */
    private String bottomLogo;


    public void saveOrUpdate(Long id, IPlatformClientConfigService platformClientConfigService) {
        boolean isEdit = id == null;
        PlatformClientConfig config;
        if (isEdit) {
            config = platformClientConfigService.getById(id);
        } else {
            config = platformClientConfigService.lambdaQuery()
                    .eq(PlatformClientConfig::getType, getType())
                    .one();
            if (config == null) {
                config = new PlatformClientConfig();
            }
        }

        config.setType(getType())
                .setLoginLogo(getLoginLogo())
                .setLoginBackground(getLoginBackground())
                .setCopyright(getCopyright())
                .setTitleLogo(getTitleLogo())
                .setBottomLogo(getBottomLogo());

        boolean success = config.getId() == null ?
                platformClientConfigService.save(config) :
                platformClientConfigService.updateById(config);
        if (!success) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "新增/更新失败");
        }


    }


    public void checkParam() {
        switch (getType()) {
            case PLATFORM:
            case SHOP:
                if (
                        StrUtil.isBlank(getLoginLogo()) ||
                                StrUtil.isBlank(getLoginBackground()) ||
                                StrUtil.isBlank(getTitleLogo())
                ) {
                    throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
                }
                setBottomLogo(null);
                break;
            case APP:
            case H5:
            case MINI_PROGRAM:
                if (StrUtil.isBlank(getBottomLogo())) {
                    throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
                }
                setLoginLogo(null);
                setLoginBackground(null);
                setTitleLogo(null);
                break;
            default:
                break;
        }
    }
}
