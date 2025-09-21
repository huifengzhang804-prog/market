package com.medusa.gruul.addon.platform.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.platform.model.dto.DecorationPageCreateDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationPageModifyDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationPageOfTemplateDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationPageQueryDTO;
import com.medusa.gruul.addon.platform.model.enums.PlatformError;
import com.medusa.gruul.addon.platform.model.enums.TemplateTypeEnum;
import com.medusa.gruul.addon.platform.model.vo.DecorationPageDetailVO;
import com.medusa.gruul.addon.platform.model.vo.DecorationPageVO;
import com.medusa.gruul.addon.platform.mp.entity.DecorationPage;
import com.medusa.gruul.addon.platform.mp.mapper.DecorationPageMapper;
import com.medusa.gruul.addon.platform.mp.service.IDecorationPageService;
import com.medusa.gruul.addon.platform.util.PlatformUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>平台装修模版服务接口实现类</p>
 *
 * @author Andy.Yan
 */
@Service
@RequiredArgsConstructor
public class DecorationPageServiceImpl extends ServiceImpl<DecorationPageMapper, DecorationPage> implements
        IDecorationPageService {


    /**
     * 创建装修页面
     * todo 🤔 多线程下需要加锁
     *
     * @param param {@link DecorationPageCreateDTO}
     * @return {@link DecorationPage#getId()}
     */
    @Override
    public Long create(DecorationPageCreateDTO param) {
        // 检查参数
        param.validParam();
        //是否是店铺装修页面
        boolean isShop = TemplateTypeEnum.SHOP == param.getTemplateType();
        // 检查页面名称的唯一性by page_type + template_type + business_type + endpoint_type
        boolean exists = this.lambdaQuery()
                .eq(DecorationPage::getName, param.getName())
                .eq(DecorationPage::getType, param.getType())
                .eq(DecorationPage::getTemplateType, param.getTemplateType())
                .eq(DecorationPage::getEndpointType, param.getEndpointType())
                .eq(isShop, DecorationPage::getBusinessType, param.getBusinessType())
                .isNull(!isShop, DecorationPage::getBusinessType)
                .exists();
        PlatformError.PAGE_NAME_ALREADY_EXIST.trueThrow(exists);
        //保存数据
        DecorationPage decorationPage = new DecorationPage();
        BeanUtil.copyProperties(param, decorationPage);
        PlatformError.FAILED_TO_CREATE_PAGE.falseThrow(this.save(decorationPage));
        return decorationPage.getId();
    }

    /**
     * 修改装修页面
     * todo 🤔 多线程下需要加锁
     *
     * @param param 装修页面modify param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modify(DecorationPageModifyDTO param) {
        // 检查入参
        param.validParam();
        // 查询页面数据
        DecorationPage page = this.lambdaQuery()
                .select(DecorationPage::getEndpointType, DecorationPage::getType, DecorationPage::getCustomType)
                .eq(DecorationPage::getId, param.getId())
                .one();
        // 检查页面数据是否存在
        if (page == null) {
            throw PlatformError.PAGE_NOT_EXIST.exception();
        }
        boolean isShop = TemplateTypeEnum.SHOP == param.getTemplateType();
        // 检查页面名称的唯一性by page_type + template_type + business_type + endpoint_type
        boolean exists = this.lambdaQuery()
                .ne(DecorationPage::getId, param.getId())
                .eq(DecorationPage::getName, param.getName())
                .eq(DecorationPage::getType, param.getType())
                .eq(DecorationPage::getTemplateType, param.getTemplateType())
                .eq(DecorationPage::getEndpointType, param.getEndpointType())
                .eq(isShop, DecorationPage::getBusinessType, param.getBusinessType())
                .isNull(!isShop, DecorationPage::getBusinessType)
                .exists();
        PlatformError.PAGE_NAME_ALREADY_EXIST.trueThrow(exists);

        //缓存双删 删除之前缓存的当前页面的数据
        RedisUtil.doubleDeletion(
                () -> {
                    boolean update = this.lambdaUpdate()
                            .set(DecorationPage::getProperties, param.getProperties())
                            .set(DecorationPage::getName, param.getName())
                            .set(DecorationPage::getRemark, param.getRemark())
                            .eq(DecorationPage::getId, param.getId())
                            .update();
                    PlatformError.FAILED_TO_UPDATE_PAGE.falseThrow(update);
                },
                PlatformUtil.pageCacheKey(page.getEndpointType(), page.getType(), page.getCustomType())
        );

    }

    /**
     * 分页查询页面信息
     *
     * @param param 分页参数
     * @return {@link DecorationPageVO}
     */
    @Override
    public IPage<DecorationPageVO> pageQuery(DecorationPageQueryDTO param) {
        boolean isShop = TemplateTypeEnum.SHOP == param.getTemplateType();
        Page<DecorationPage> decorationPages = this.lambdaQuery()
                .eq(DecorationPage::getTemplateType, param.getTemplateType())
                .eq(StrUtil.isNotEmpty(param.getCustomType()), DecorationPage::getCustomType, param.getCustomType())
                .eq(isShop, DecorationPage::getBusinessType, param.getBusinessType())
                .like(StrUtil.isNotEmpty(param.getName()), DecorationPage::getName, param.getName())
                .isNull(!isShop, DecorationPage::getBusinessType)
                .eq(DecorationPage::getEndpointType, param.getEndpointType())
                .eq(param.getType() != null, DecorationPage::getType, param.getType())
                .page(new Page<>(param.getCurrent(), param.getSize()));
        return decorationPages.convert(
                page -> {
                    DecorationPageVO vo = new DecorationPageVO();
                    BeanUtil.copyProperties(page, vo);
                    return vo;
                }
        );
    }

    /**
     * 逻辑删除页面,检查当前页面是否被模板引用,如果被引用则不能删除.
     *
     * @param id 页面ID
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查页面是否存在
        DecorationPage decorationPage = this.lambdaQuery().eq(DecorationPage::getId, id).one();
        if (decorationPage == null) {
            throw PlatformError.PAGE_NOT_EXIST.exception();
        }
        // 检查页面是否被模板引用
        Integer referencedCount = this.baseMapper.referencedCount(id);
        //页面被模板引用则不能删除
        PlatformError.REFERENCED_BY_TEMPLATE.trueThrow(referencedCount > 0);
        //页面缓存双删
        RedisUtil.doubleDeletion(
                () -> {
                    // 删除页面
                    PlatformError.FAILED_TO_DELETE_PAGE.falseThrow(this.removeById(id));
                },
                // 使用 pipeline 删除页面缓存 、删除副本数量缓存
                () -> RedisUtil.executePipelined(
                        operations -> {
                            //删除装修页面缓存
                            operations.delete(PlatformUtil.pageCacheKey(decorationPage.getEndpointType(),
                                    decorationPage.getType(), decorationPage.getCustomType()));
                            //删除副本数量缓存
                            operations.delete(PlatformUtil.pageCopyCacheKey(id));
                        }
                )
        );

    }

    /**
     * 复制页面
     *
     * @param id 页面ID
     */
    @Override
    public void clone(Long id) {
        // 检查页面是否存在
        DecorationPage decorationPage = this.lambdaQuery().eq(DecorationPage::getId, id).one();
        if (decorationPage == null) {
            throw PlatformError.PAGE_NOT_EXIST.exception();
        }
        // 在redis中把当前id对应的的副本数量 +1 并获取
        RedisUtil.increment(
                PlatformUtil.pageCopyCacheKey(id),
                CommonPool.NUMBER_ONE,
                (count) -> {
                    //副本名称
                    String copyName = PlatformUtil.copyName(decorationPage.getName(), count);
                    //检查副本名称长度
                    PlatformError.PAGE_NAME_LENGTH_TOO_LONG.trueThrow(copyName.length() > CommonPool.NUMBER_TEN);
                    // 检查页面名称的唯一性by page_type + template_type + business_type + endpoint_type
                    boolean isShopDecoration = TemplateTypeEnum.SHOP == decorationPage.getTemplateType();
                    boolean existed = this.lambdaQuery()
                            .eq(DecorationPage::getName, copyName)
                            .eq(DecorationPage::getType, decorationPage.getType())
                            .eq(DecorationPage::getTemplateType, decorationPage.getTemplateType())
                            .eq(isShopDecoration, DecorationPage::getBusinessType, decorationPage.getBusinessType())
                            .isNull(!isShopDecoration, DecorationPage::getBusinessType)
                            .eq(DecorationPage::getEndpointType, decorationPage.getEndpointType())
                            .exists();
                    PlatformError.PAGE_NAME_ALREADY_EXIST.trueThrow(existed);
                    // 渲染复制对象
                    DecorationPage copyOfPage = new DecorationPage()
                            .setRemark(decorationPage.getRemark())
                            .setName(copyName)
                            .setType(decorationPage.getType())
                            .setProperties(decorationPage.getProperties())
                            .setBusinessType(decorationPage.getBusinessType())
                            .setEndpointType(decorationPage.getEndpointType())
                            .setTemplateType(decorationPage.getTemplateType());
                    //保存副本数据
                    PlatformError.TEMPLATE_CREATION_FAILED.falseThrow(this.save(copyOfPage));
                }
        );

    }

    /**
     * 按照页面类型查询所有页面
     *
     * @param dto {@link PageTypeEnum}
     * @return {@link DecorationPage}
     */
    @Override
    public List<DecorationPage> listPageByPageType(DecorationPageOfTemplateDTO dto) {
        return this.lambdaQuery()
                .select(DecorationPage::getId, DecorationPage::getType, DecorationPage::getName)
                .eq(DecorationPage::getTemplateType, dto.getTemplateType())
                .eq(dto.getBusinessType() != null, DecorationPage::getBusinessType, dto.getBusinessType())
                .eq(DecorationPage::getEndpointType, dto.getEndpointType())
                .list();
    }

    /**
     * 根据ID查询页面
     *
     * @param pageIds 页面ID
     * @return {@link }
     */
    @Override
    public List<DecorationPage> listPageByIds(List<Long> pageIds) {
        return this.lambdaQuery().in(DecorationPage::getId, pageIds).list();
    }

    /**
     * 获取页面详情
     *
     * @param id 页面ID
     * @return {@link DecorationPageDetailVO}
     */
    @Override
    public DecorationPageDetailVO getPageById(Long id) {
        DecorationPageDetailVO result = new DecorationPageDetailVO();
        DecorationPage page = this.getById(id);
        PlatformError.PAGE_NOT_EXIST.trueThrow(page == null);
        BeanUtil.copyProperties(page, result);
        return result;
    }
}
