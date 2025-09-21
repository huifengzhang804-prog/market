package com.medusa.gruul.addon.invoice.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceHeaderCreateDTO;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceHeaderQueryDTO;
import com.medusa.gruul.addon.invoice.model.dto.SetDefaultInvoiceHeaderDTO;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceError;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceHeaderOwnerTypeEnum;
import com.medusa.gruul.addon.invoice.model.vo.InvoiceHeadVO;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceHeader;
import com.medusa.gruul.addon.invoice.mp.service.IInvoiceHeaderService;
import com.medusa.gruul.addon.invoice.service.InvoiceHeaderHandlerService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class InvoiceHeaderHandlerServiceImpl implements InvoiceHeaderHandlerService {

    private final IInvoiceHeaderService invoiceHeaderService;
    private final ShopRpcService shopRpcService;
    private final UaaRpcService uaaRpcService;

    private ShopInfoVO getShopInfo(Long shopId) {
        ShopInfoVO shopInfoVO = this.shopRpcService.getShopInfoByShopId(shopId);
        InvoiceError.SHOP_NOT_EXIST.trueThrow(shopInfoVO == null);
        return shopInfoVO;
    }

    private UserInfoVO getUserInfo(Long userId) {
        Option<UserInfoVO> optional = uaaRpcService.getUserDataByUserId(userId);
        InvoiceError.USER_NOT_EXIST.trueThrow(optional.isEmpty());
        return optional.get();
    }

    /**
     * <p>创建发票抬头</p>
     * <p>首先根据{@link InvoiceHeaderCreateDTO#getOwnerType()}检查用户或者店铺</p>
     * <p>如果检查通过,则查询表中当前Owner是否创建过发票抬头,若没有则将当前发票抬头设置为默认</p>
     *
     * @param dto {@link InvoiceHeaderCreateDTO}
     */
    @Override
    public void create(InvoiceHeaderCreateDTO dto) {
        switch (dto.getOwnerType()) {
            case SHOP -> {
                ShopInfoVO shopInfo = getShopInfo(dto.getOwnerId());
                dto.setOwnerId(shopInfo.getId());
            }
            case USER -> {
                UserInfoVO userInfo = getUserInfo(dto.getOwnerId());
                dto.setOwnerId(userInfo.getUserId());
            }
            default -> throw InvoiceError.INVOICE_HEADER_INVALID_OWNER_TYPE.exception("无效的OwnerType");
        }
        InvoiceHeader invoiceHeader = new InvoiceHeader()
                .setType(dto.getInvoiceHeaderType())
                .setOwnerType(dto.getOwnerType())
                .setOwnerId(dto.getOwnerId())
                .setHeader(dto.getHeader())
                .setTaxIdentNo(dto.getTaxIdentNo())
                .setOpeningBank(dto.getOpeningBank())
                .setBankAccountNo(dto.getBankAccountNo())
                .setEnterprisePhone(dto.getEnterprisePhone())
                .setEnterpriseAddress(dto.getEnterpriseAddress())
                .setEmail(dto.getEmail());
        invoiceHeader.setId(dto.getId());
        if (invoiceHeaderService.lambdaQuery().eq(InvoiceHeader::getOwnerId, dto.getOwnerId())
                .eq(InvoiceHeader::getOwnerType, dto.getOwnerType())
                .eq(InvoiceHeader::getDeleted, Boolean.FALSE)
                .count() == CommonPool.NUMBER_ZERO) {
            invoiceHeader.setIsDefault(Boolean.TRUE);
        } else {
            if (Objects.isNull(invoiceHeader.getId())) {
                invoiceHeader.setIsDefault(Boolean.FALSE);
            }

        }
        invoiceHeaderService.saveOrUpdate(invoiceHeader);
    }

    /**
     * 分页查询发票抬头
     *
     * @param invoiceHeaderQuery 查询参数
     * @return 发票抬头列表
     */
    @Override
    public IPage<InvoiceHeadVO> pageInvoiceHeader(InvoiceHeaderQueryDTO invoiceHeaderQuery) {
        ISecurity.match()
                .ifUser(user -> invoiceHeaderQuery.setOwnerId(user.getId()))
                .ifAnyShopAdmin(secureUser -> invoiceHeaderQuery.setOwnerId(secureUser.getShopId()));
        return invoiceHeaderService.pageInvoiceHeader(invoiceHeaderQuery);
    }


    /**
     * 删除发票抬头
     *
     * @param invoiceHeaderId 删除发票抬头
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInvoiceHeader(Long invoiceHeaderId) {

        // 检查发票抬头是否存在
        InvoiceHeader invoiceHeader = invoiceHeaderService.lambdaQuery()
                .eq(InvoiceHeader::getId, invoiceHeaderId).one();
        InvoiceError.INVOICE_HEADER_DOES_NOT_EXIST.trueThrow(invoiceHeader == null);

        // 如果当前删除的发票抬头非默认
        if (!invoiceHeader.getIsDefault()) {
            invoiceHeaderService.lambdaUpdate()
                    .eq(InvoiceHeader::getId, invoiceHeaderId)
                    .remove();
            return;
        }

        // 否则根据owner查询其他发票抬头并设置第一个为默认
        List<InvoiceHeader> headers = invoiceHeaderService.lambdaQuery()
                .eq(InvoiceHeader::getOwnerId, invoiceHeader.getOwnerId())
                .eq(InvoiceHeader::getOwnerType, invoiceHeader.getOwnerType())
                .eq(InvoiceHeader::getIsDefault, Boolean.FALSE)
                .list();
        if (!CollectionUtils.isEmpty(headers)) {
            InvoiceError.INVOICE_HEADER_SET_DEFAULT_FAIL.falseThrow(invoiceHeaderService.lambdaUpdate()
                    .set(InvoiceHeader::getIsDefault, Boolean.TRUE)
                    .eq(InvoiceHeader::getId, headers.get(0).getId())
                    .update());
        }
        invoiceHeaderService.lambdaUpdate()
                .eq(InvoiceHeader::getId, invoiceHeaderId)
                .remove();
    }

    /**
     * 设置默认抬头
     *
     * @param dto {@link SetDefaultInvoiceHeaderDTO}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultInvoiceHeader(SetDefaultInvoiceHeaderDTO dto) {
        InvoiceHeader invoiceHeader = invoiceHeaderService.lambdaQuery()
                .eq(InvoiceHeader::getId, dto.getId())
                .one();
        InvoiceError.INVOICE_HEADER_DOES_NOT_EXIST.trueThrow(invoiceHeader == null);
        // 检查是否存在默认抬头
        InvoiceHeader existDefaultHeader = invoiceHeaderService.lambdaQuery()
                .eq(InvoiceHeader::getOwnerId, dto.getOwnerId())
                .eq(InvoiceHeader::getOwnerType, dto.getOwnerType())
                .eq(InvoiceHeader::getIsDefault, Boolean.TRUE)
                .ne(InvoiceHeader::getId, dto.getId())
                .one();
        if (existDefaultHeader != null) {
            InvoiceError.INVOICE_HEADER_SET_DEFAULT_FAIL.falseThrow(
                    invoiceHeaderService.lambdaUpdate()
                            .set(InvoiceHeader::getIsDefault, Boolean.FALSE)
                            .eq(InvoiceHeader::getId, existDefaultHeader.getId())
                            .update()
            );
        }
        InvoiceError.INVOICE_HEADER_SET_DEFAULT_FAIL.falseThrow(
                invoiceHeaderService.lambdaUpdate()
                        .set(InvoiceHeader::getIsDefault, Boolean.TRUE)
                        .eq(InvoiceHeader::getId, dto.getId())
                        .update()
        );


    }

    /**
     * 获取发票抬头详情
     *
     * @param invoiceHeaderId        发票抬头id
     * @param invoiceHeaderOwnerType 发票抬头所属方类型
     */
    @Override
    public InvoiceHeadVO getInvoiceHeaderDetail(Long invoiceHeaderId, InvoiceHeaderOwnerTypeEnum invoiceHeaderOwnerType) {
        InvoiceHeader invoiceHeader = invoiceHeaderService.lambdaQuery()
                .eq(InvoiceHeader::getId, invoiceHeaderId)
                .eq(InvoiceHeader::getOwnerType, invoiceHeaderOwnerType)
                .one();
        if (invoiceHeader == null) {
            throw InvoiceError.INVOICE_HEADER_DOES_NOT_EXIST.exception();
        }
        return new InvoiceHeadVO()
                .setId(invoiceHeader.getId())
                .setInvoiceHeaderType(invoiceHeader.getType())
                .setEmail(invoiceHeader.getEmail())
                .setOwnerId(invoiceHeader.getOwnerId())
                .setOwnerType(invoiceHeader.getOwnerType())
                .setIsDefault(invoiceHeader.getIsDefault())
                .setHeader(invoiceHeader.getHeader())
                .setTaxIdentNo(invoiceHeader.getTaxIdentNo())
                .setOpeningBank(invoiceHeader.getOpeningBank())
                .setBankAccountNo(invoiceHeader.getBankAccountNo())
                .setEnterprisePhone(invoiceHeader.getEnterprisePhone())
                .setEnterpriseAddress(invoiceHeader.getEnterpriseAddress());

    }


    @Override
    public InvoiceHeadVO getDefaultInvoiceHeader(Long ownId, InvoiceHeaderOwnerTypeEnum invoiceHeaderOwnerType) {
        InvoiceHeader invoiceHeader = invoiceHeaderService.lambdaQuery()
                .select(InvoiceHeader::getId, InvoiceHeader::getHeader)
                .eq(InvoiceHeader::getIsDefault, Boolean.TRUE)
                .eq(InvoiceHeader::getOwnerId, ownId)
                .eq(InvoiceHeader::getOwnerType, invoiceHeaderOwnerType)
                .one();
        if (invoiceHeader == null) {
            return null;
        }
        return new InvoiceHeadVO()
                .setId(invoiceHeader.getId())
                .setHeader(invoiceHeader.getHeader());
    }

}
