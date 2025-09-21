package com.medusa.gruul.shop.service.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import com.medusa.gruul.shop.api.model.dto.DecorationCopyPageDTO;
import com.medusa.gruul.shop.api.model.dto.DecorationCopyPageParamDTO;
import com.medusa.gruul.shop.service.model.dto.*;
import com.medusa.gruul.shop.service.model.enums.ShopError;
import com.medusa.gruul.shop.service.model.vo.ShopDecorationPageDetailsVO;
import com.medusa.gruul.shop.service.mp.entity.ShopDecorationPage;
import com.medusa.gruul.shop.service.mp.mapper.ShopDecorationPageMapper;
import com.medusa.gruul.shop.service.mp.service.IShopDecorationPageService;
import com.medusa.gruul.shop.service.service.addon.ShopAddonSupporter;
import com.medusa.gruul.shop.service.util.ShopUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Andy.Yan
 */
@Service
@RequiredArgsConstructor
public class ShopDecorationPageImpl extends ServiceImpl<ShopDecorationPageMapper, ShopDecorationPage> implements
        IShopDecorationPageService {


    private final ShopAddonSupporter shopAddonSupporter;

    /**
     * 创建店铺装修页面对象
     *
     * @param param 店铺装修页面对象,参考{@link ShopDecorationPageCreateDTO}
     * @return {@link ShopDecorationPage#getId()}
     */
    @Override
    public Long create(ShopDecorationPageCreateDTO param) {
        // 检查页面name是否存在
        boolean exists = TenantShop.disable(() -> this.lambdaQuery()
                .eq(ShopDecorationPage::getName, param.getName())
                .eq(ShopDecorationPage::getShopId, ISecurity.userMust().getShopId())
                .eq(ShopDecorationPage::getBusinessType, param.getBusinessType())
                .eq(ShopDecorationPage::getEndpointType, param.getEndpointType())
                .eq(ShopDecorationPage::getType, param.getType())
                .exists()
        );
        ShopError.PAGE_NAME_ALREADY_EXIST.trueThrow(exists);
        // 创建装修页面对象
        ShopDecorationPage newedPage = new ShopDecorationPage();
        BeanUtil.copyProperties(param, newedPage);
        ShopError.FAILED_TO_CREATE_PAGE.falseThrow(this.save(newedPage));
        return newedPage.getId();
    }

    /**
     * 编辑店铺装修页面对象
     *
     * @param param 店铺装修页面对象,参考{@link ShopDecorationPageModifyDTO}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modify(ShopDecorationPageModifyDTO param) {
        // 检查页面是否存在
        Long shopId = ISecurity.userMust().getShopId();

        ShopDecorationPage page = TenantShop.disable(
                () -> this.lambdaQuery()
                        .eq(ShopDecorationPage::getId, param.getId())
                        .eq(ShopDecorationPage::getShopId, shopId)
                        .one()
        );
        if (page == null) {
            throw ShopError.PAGE_NOT_EXIST.exception();
        }
        // 检查页面name是否存在
        boolean exists = TenantShop.disable(() -> this.lambdaQuery()
                .ne(ShopDecorationPage::getId, param.getId())
                .eq(ShopDecorationPage::getName, param.getName())
                .eq(ShopDecorationPage::getShopId, shopId)
                .eq(ShopDecorationPage::getBusinessType, param.getBusinessType())
                .eq(ShopDecorationPage::getEndpointType, param.getEndpointType())
                .eq(ShopDecorationPage::getType, param.getType())
                .exists()
        );
        ShopError.PAGE_NAME_ALREADY_EXIST.trueThrow(exists);

        RedisUtil.doubleDeletion(
                () -> {
                    // 修改页面对象
                    boolean update = TenantShop.disable(() -> this.lambdaUpdate()
                            .set(ShopDecorationPage::getName, param.getName())
                            .set(ShopDecorationPage::getRemark, param.getRemark())
                            .set(ShopDecorationPage::getProperties, param.getProperties())
                            .eq(ShopDecorationPage::getId, param.getId())
                            .eq(ShopDecorationPage::getShopId, shopId)
                            .update()
                    );
                    ShopError.FAILED_TO_MODIFY_PAGE.falseThrow(update);
                },
                ShopUtil.pageCacheKey(page.getShopId(), page.getEndpointType(), page.getType(), page.getCustomType())
        );
    }

    /**
     * 克隆店铺装修页面对象
     *
     * @param param 被克隆的页面对象,参考{@link ShopDecorationPageCloneDTO}
     */
    @Override
    public void clone(ShopDecorationPageCloneDTO param) {
        Long shopId = ISecurity.userMust().getShopId();
        // 检查页面是否存在
        Long pageId = param.getId();
        ShopDecorationPage page = TenantShop.disable(
                () -> this.lambdaQuery()
                        .eq(ShopDecorationPage::getId, pageId)
                        .eq(ShopDecorationPage::getShopId, shopId)
                        .one()
        );
        if (page == null) {
            throw ShopError.PAGE_NOT_EXIST.exception();
        }
        RedisUtil.increment(
                ShopUtil.pageCopyCacheKey(shopId, pageId),
                CommonPool.NUMBER_ONE,
                (count) -> {
                    //副本名称
                    String clonedName = ShopUtil.copyName(page.getName(), count);
                    ShopError.PAGE_NAME_LENGTH_TOO_LONG.trueThrow(clonedName.length() > CommonPool.NUMBER_TEN);
                    // 检查页面name是否存在
                    boolean exists = TenantShop.disable(() -> this.lambdaQuery()
                            .eq(ShopDecorationPage::getName, clonedName)
                            .eq(ShopDecorationPage::getShopId, shopId)
                            .eq(ShopDecorationPage::getBusinessType, page.getBusinessType())
                            .eq(ShopDecorationPage::getEndpointType, page.getEndpointType())
                            .eq(ShopDecorationPage::getType, page.getType())
                            .exists()
                    );
                    ShopError.PAGE_NAME_ALREADY_EXIST.trueThrow(exists);
                    ShopDecorationPage clonedPage = new ShopDecorationPage()
                            .setName(clonedName)
                            .setRemark(page.getRemark())
                            .setProperties(page.getProperties())
                            .setEndpointType(page.getEndpointType())
                            .setBusinessType(page.getBusinessType())
                            .setShopId(shopId)
                            .setType(page.getType());
                    ShopError.FAILED_TO_CLONE_PAGE.falseThrow(this.save(clonedPage));
                }
        );
    }

    /**
     * 删除店铺装修页面对象
     *
     * @param param 参数
     */
    @Override
    public void delete(ShopDecorationPageDeleteDTO param) {
        // 检查页面是否存在
        Long shopId = ISecurity.userMust().getShopId();
        Long pageId = param.getId();
        ShopDecorationPage sourcePage = TenantShop.disable(
                () -> this.lambdaQuery()
                        .eq(ShopDecorationPage::getId, pageId)
                        .eq(ShopDecorationPage::getShopId, shopId)
                        .one()
        );
        if (sourcePage == null) {
            throw ShopError.PAGE_NOT_EXIST.exception();
        }
        // 检查当前页面是否被模板引用
        Integer referencedCount = this.baseMapper.referencedCount(shopId, pageId);
        ShopError.REFERENCED_BY_TEMPLATE.trueThrow(referencedCount > CommonPool.NUMBER_ZERO);
        // 逻辑删除页面
        RedisUtil.doubleDeletion(
                () -> {
                    boolean remove = TenantShop.disable(
                            () -> this.lambdaUpdate()
                                    .eq(ShopDecorationPage::getId, pageId)
                                    .eq(ShopDecorationPage::getShopId, shopId)
                                    .remove()
                    );
                    ShopError.FAILED_TO_DELETE_PAGE.falseThrow(remove);
                },
                ShopUtil.pageCopyCacheKey(sourcePage.getShopId(), pageId)
        );


    }

    /**
     * 根据ID获取店铺装修页面详情
     *
     * @param id 页面ID
     * @return {@link ShopDecorationPageDetailsVO}
     */
    @Override
    public ShopDecorationPageDetailsVO getShopPageById(Long id) {
        ShopDecorationPage sourcePage = this.lambdaQuery()
                .eq(ShopDecorationPage::getId, id)
                .one();
        ShopError.PAGE_NOT_EXIST.trueThrow(sourcePage == null);
        ShopDecorationPageDetailsVO result = new ShopDecorationPageDetailsVO();
        BeanUtil.copyProperties(sourcePage, result);
        return result;
    }

    /**
     * 分页查询店铺装修页面数据
     *
     * @param dto 查询对象 {@link ShopDecorationPagePagingQueryDTO}
     * @return {@link ShopDecorationPageDetailsVO}
     */
    @Override
    public IPage<ShopDecorationPageDetailsVO> pageQuery(ShopDecorationPagePagingQueryDTO dto) {
        // 分页查询
        Page<ShopDecorationPage> page = this.lambdaQuery()
                .eq(ShopDecorationPage::getShopId, ISecurity.userMust().getShopId())
                .eq(dto.getBusinessType() != null, ShopDecorationPage::getBusinessType, dto.getBusinessType())
                .eq(dto.getEndpointType() != null, ShopDecorationPage::getEndpointType, dto.getEndpointType())
                .eq(dto.getType() != null, ShopDecorationPage::getType, dto.getType())
                .eq(StrUtil.isNotEmpty(dto.getCustomType()), ShopDecorationPage::getCustomType, dto.getCustomType())
                .page(new Page<>(dto.getCurrent(), dto.getSize()));
        return page.convert(
                record -> {
                    ShopDecorationPageDetailsVO vo = new ShopDecorationPageDetailsVO();
                    BeanUtil.copyProperties(record, vo);
                    return vo;
                }
        );
    }

    /**
     * 根据店铺ID+页面类型获取页面集合
     *
     * @param endpointType     店铺ID
     * @param businessTypeEnum 业务类型
     * @return {@link ShopDecorationPage}
     */
    @Override
    public List<ShopDecorationPage> listPageByShopIdAndPageType(TemplateBusinessTypeEnum businessTypeEnum,
                                                                DecorationEndpointTypeEnum endpointType) {
        return this.lambdaQuery()
                .eq(ShopDecorationPage::getEndpointType, endpointType)
                .eq(ShopDecorationPage::getBusinessType, businessTypeEnum)
                .list();
    }

    @Override
    public void clonePlatformPage(DecorationCopyPageParamDTO param) {
        Long shopId = ISecurity.userMust().getShopId();
        DecorationCopyPageDTO copyPage = shopAddonSupporter.platformPage(param);
        if (copyPage == null) {
            return;
        }
        ShopDecorationPage page = new ShopDecorationPage()
                .setShopId(shopId)
                .setName(copyPage.getName())
                .setRemark(ShopUtil.copiedDesc())
                .setProperties(copyPage.getProperties())
                .setType(param.getPageType())
                .setBusinessType(param.getBusiness())
                .setEndpointType(param.getEndpoint());
        ShopError.FAILED_TO_CLONE_PAGE.falseThrow(TenantShop.disable(() -> this.save(page)));
    }

}
