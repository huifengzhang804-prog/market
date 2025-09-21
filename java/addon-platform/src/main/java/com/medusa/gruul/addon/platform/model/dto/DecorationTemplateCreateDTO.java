package com.medusa.gruul.addon.platform.model.dto;

import com.medusa.gruul.addon.platform.model.enums.PlatformError;
import com.medusa.gruul.addon.platform.model.enums.TemplateTypeEnum;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import com.medusa.gruul.shop.api.model.bo.PageReference;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p></p>
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class DecorationTemplateCreateDTO implements BaseDTO {

    @Serial
    private static final long serialVersionUID = -3762448023648263959L;

    /**
     * 模板名称
     */
    @NotNull
    @Length(min = 1, max = 10)
    private String name;

    /**
     * 模版说明
     */
    @NotNull
    @Length(min = 1, max = 20)
    private String description;

    /**
     * 组成模版的页面
     */
    @NotNull
    @Size(min = 1)
    private List<PageReference> pages;

    /**
     * 模板类型
     */
    @NotNull
    private TemplateTypeEnum templateType;

    /**
     * 模板业务类型
     */
    private TemplateBusinessTypeEnum businessType;

    /**
     * 模板终端类型
     */
    @NotNull
    private DecorationEndpointTypeEnum endpointType;


    /**
     * 参数校验
     */
    @Override
    public void validParam() {
        if (TemplateTypeEnum.PLATFORM == getTemplateType()) {
            platformTemplateValid();
            return;
        }
        shopTemplateValid();
    }

    /**
     * 平台模板页面校验
     */
    private void platformTemplateValid() {
        Map<PageTypeEnum, Long> pageCountMap = this.getPages()
                .stream()
                .filter(page -> page.getPageId() != null)
                .collect(
                        Collectors.groupingBy(PageReference::getPageType, Collectors.counting())
                );
        //校验是否包含非平台页面
        Set<PageTypeEnum> paramPageTypes = pageCountMap.keySet();
        if (paramPageTypes.stream().anyMatch(type -> !type.isPlatform())) {
            throw PlatformError.PAGE_TYPE_INVALID.dataEx("page type invalid: " + paramPageTypes);
        }
        //所有必须的页面
        Set<PageTypeEnum> requiredPage = Set.of(PageTypeEnum.PRODUCT_CATEGORY_PAGE, PageTypeEnum.BOTTOM_NAVIGATION_PAGE,
                PageTypeEnum.PERSONAL_CENTER_PAGE);
        //校验是否包含所有必须的页面
        if (!paramPageTypes.containsAll(requiredPage)) {
            throw PlatformError.PAGE_TYPE_INVALID.dataEx("required page error");
        }
        if (!(paramPageTypes.contains(PageTypeEnum.SAME_CITY_MALL_HOME_PAGE) || paramPageTypes.contains(PageTypeEnum.RECOMMENDED_MALL_HOME_PAGE))){
            throw PlatformError.PAGE_TYPE_INVALID.dataEx("Home must exist in the same city");
        }
        //除自定义页面外 每个页面只能有一个
        pageCountMap.forEach(
                (pageType, pageCount) -> {
                    if (PageTypeEnum.CUSTOMIZED_PAGE == pageType) {
                        return;
                    }
                    if (pageCount > 1) {
                        throw PlatformError.PAGE_TYPE_INVALID.dataEx("page count error: " + pageType + " " + pageCount);
                    }
                }
        );
    }

    /**
     * 店铺模板页面校验
     */
    private void shopTemplateValid() {
        //校验业务类型，店铺模板必须有业务类型
        TemplateBusinessTypeEnum businessType = this.getBusinessType();
        if (businessType == null) {
            throw SystemCode.PARAM_VALID_ERROR.dataEx("businessType cannot be null");
        }

        Map<PageTypeEnum, Long> pageCountMap = this.getPages()
                .stream()
                .collect(
                        Collectors.groupingBy(PageReference::getPageType, Collectors.counting())
                );
        //校验是否包含平台页面
        Set<PageTypeEnum> paramPageTypes = pageCountMap.keySet();
        if (paramPageTypes.stream().anyMatch(PageTypeEnum::isPlatform)) {
            throw PlatformError.PAGE_TYPE_INVALID.dataEx("page type invalid: " + paramPageTypes);
        }
        //如果是 O2O 业务类型
        if (TemplateBusinessTypeEnum.O2O == businessType) {
            Long homeCount;
            //O2O仅允许有一个店铺首页 并且只能有一个
            if (pageCountMap.size() != 1 || (homeCount = pageCountMap.get(PageTypeEnum.SHOP_HOME_PAGE)) == null
                    || homeCount != 1) {
                throw PlatformError.PAGE_TYPE_INVALID.dataEx("O2O home page error");
            }
            return;
        }
        //如果不是 O2O 除自定义页面外 每个页面只能有一个 并且必须包含店铺首页 店铺分类 底部导航
        //所有必须的页面
        Set<PageTypeEnum> requiredPage = Set.of(PageTypeEnum.SHOP_HOME_PAGE, PageTypeEnum.SHOP_CATEGORY_PAGE,
                PageTypeEnum.SHOP_BOTTOM_NAVIGATION_PAGE);
        //校验是否包含所有必须的页面
        if (!paramPageTypes.containsAll(requiredPage)) {
            throw PlatformError.PAGE_TYPE_INVALID.dataEx("required page error");
        }
        //除自定义页面外 每个页面只能有一个
        pageCountMap.forEach(
                (pageType, pageCount) -> {
                    if (PageTypeEnum.SHOP_CUSTOMIZED_PAGE == pageType) {
                        return;
                    }
                    if (pageCount > 1) {
                        throw PlatformError.PAGE_TYPE_INVALID.dataEx("page count error: " + pageType + " " + pageCount);
                    }
                }
        );
    }

}
