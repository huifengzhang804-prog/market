package com.medusa.gruul.addon.freight.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.freight.model.dto.LogisticsExpressDTO;
import com.medusa.gruul.addon.freight.model.enums.LogisticsCompanyStatus;
import com.medusa.gruul.addon.freight.model.param.LogisticsExpressParam;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressUsableVO;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressVO;
import com.medusa.gruul.addon.freight.mp.entity.FreightConf;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsExpress;
import com.medusa.gruul.addon.freight.mp.service.IFrightConfService;
import com.medusa.gruul.addon.freight.mp.service.ILogisticsExpressService;
import com.medusa.gruul.addon.freight.service.LogisticsExpressInfoService;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.shop.api.enums.SelfShopEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author xiaoq
 * @Description
 * @date 2022-06-20 09:29
 */
@Service
@RequiredArgsConstructor
public class LogisticsExpressInfoServiceImpl implements LogisticsExpressInfoService {

    private final IFrightConfService frightConfService;

    private final ILogisticsExpressService logisticsExpressService;

    /**
     * 物流服务新增
     *
     * @param logisticsExpressDTO 物流服务DTO
     */
    @Override
    public void addLogisticsExpress(LogisticsExpressDTO logisticsExpressDTO) {
        checkInfo(logisticsExpressDTO);
        LogisticsExpress logisticsExpress = logisticsExpressDTO.coverLogisticsExpress();
        if (!logisticsExpressService.save(logisticsExpress)) {
            throw new GlobalException("物流服务新增失败");
        }
    }

    /**
     * 物流服务修改
     *
     * @param logisticsExpressDTO 物流服务公司DTO
     */
    @Override
    public void updateLogisticsExpress(LogisticsExpressDTO logisticsExpressDTO) {
        checkInfo(logisticsExpressDTO);
        if (logisticsExpressService.getById(logisticsExpressDTO.getId()) == null) {
            throw new GlobalException("当前物流信息不存在");
        }
        LogisticsExpress logisticsExpress = logisticsExpressDTO.coverLogisticsExpress();
        boolean update = logisticsExpressService.updateById(logisticsExpress);
        if (!update) {
            throw new GlobalException("物流服务修改失败");
        }
    }

    /**
     * 物流服务删除
     *
     * @param ids 物流服务公司ids
     */
    @Override
    public void delLogisticsExpress(Long[] ids) {
        boolean remove = logisticsExpressService.lambdaUpdate()
                .in(BaseEntity::getId, Arrays.asList(ids))
                .remove();
        if (!remove) {
            throw new GlobalException("物流服务删除失败");
        }
    }

    /**
     * 获取物流服务信息
     *
     * @param logisticsExpressParam 查询参数<page>
     * @return IPage<LogisticsExpressVO>
     */
    @Override
    public IPage<LogisticsExpressVO> getLogisticsExpressList(LogisticsExpressParam logisticsExpressParam) {
        return logisticsExpressService.getLogisticsExpressList(logisticsExpressParam);
    }

    /**
     * 获取可用的物流服务信息
     *
     * @param logisticsExpressParam 查询参数<page>
     * @return IPage<LogisticsExpressUsableVO>
     */
    @Override
    public IPage<LogisticsExpressUsableVO> getLogisticsExpressUsableList(LogisticsExpressParam logisticsExpressParam) {
        return logisticsExpressService.getLogisticsExpressUsableList(logisticsExpressParam);
    }

    /**
     * 物流服务检测
     *
     * @param logisticsExpressDTO 物流服务dto
     */
    private void checkInfo(LogisticsExpressDTO logisticsExpressDTO) {
        //校验关联信息是否正常
        FreightConf freightConf = TenantShop.disable(() -> frightConfService.lambdaQuery()
                .eq(BaseEntity::getId, logisticsExpressDTO.getFreightId())
                .eq(FreightConf::getLogisticsCompanyStatus, LogisticsCompanyStatus.FORBIDDEN)
                .one());
        if (freightConf != null) {
            throw new GlobalException("当前物流不存在或已被禁用无法使用");
        }
        LogisticsExpress logisticsExpress = logisticsExpressService.lambdaQuery()
                .eq(LogisticsExpress::getFreightId, logisticsExpressDTO.getFreightId())
                .ne(logisticsExpressDTO.getId() != null, LogisticsExpress::getId, logisticsExpressDTO.getId())
                .one();
        if (logisticsExpress != null) {
            throw new GlobalException("当前快递公司已被关联,无法重新关联:".concat(logisticsExpress.getNetworkName()));
        }
        //平台 应用商家至少勾选一个
        boolean isSuperAdmin = ISecurity.matcher().any(SecureUser::getRoles, Roles.SUPER_ADMIN).match();
        if (isSuperAdmin
                && SelfShopEnum.NO.equals(logisticsExpressDTO.getDefSelfShop())
                && SelfShopEnum.NO.equals(logisticsExpressDTO.getDefSelfSupplier())) {
            throw new GlobalException("应用商家至少勾选一个");
        }
    }
}
