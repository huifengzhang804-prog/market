package com.medusa.gruul.shop.service.model.dto;

import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import com.medusa.gruul.shop.api.model.bo.PageReference;
import com.medusa.gruul.shop.service.model.enums.ShopError;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 店铺装修模板新增DTO
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class ShopDecorationTemplateCreateDTO implements BaseDTO {

    @Serial
    private static final long serialVersionUID = -6888578468400763677L;

    /**
     * 模板名称
     */
    @NotNull
    private String name;

    /**
     * 模版说明
     */
    @NotNull
    private String description;

    /**
     * 组成模版的页面
     */
    @NotNull
    @Size(min = 1)
    @Valid
    private List<PageReference> pages;

    /**
     * 模板业务类型ju
     */
    @NotNull
    private TemplateBusinessTypeEnum businessType;

    /**
     * 模板终端类型
     */
    @NotNull
    private DecorationEndpointTypeEnum endpointType;


    /**
     * 校验参数
     */
    @Override
    public void validParam() {
        //根据页面类型分组
        Map<Object, Long> pageCountMap = pages.stream()
                .collect(
                        Collectors.groupingBy(
                                PageReference::getPageType,
                                Collectors.counting()
                        )
                );
        //o2o 店铺进允许做一个店铺首页
        TemplateBusinessTypeEnum businessType = getBusinessType();
        if (TemplateBusinessTypeEnum.O2O == businessType) {
            Long homeCount;
            //O2O仅允许有一个店铺首页 并且只能有一个
            if (pageCountMap.size() != 1 || (homeCount = pageCountMap.get(PageTypeEnum.SHOP_HOME_PAGE)) == null || homeCount != 1) {
                throw ShopError.FAILED_TO_VALIDATE_TEMPLATE_PARAMETERS.dataEx("O2O home page error");
            }
            return;
        }
        //如果不是 O2O 除自定义页面外 每个页面只能有一个 并且必须包含店铺首页 店铺分类 底部导航
        //所有必须的页面
        Set<PageTypeEnum> requiredPage = Set.of(PageTypeEnum.SHOP_HOME_PAGE, PageTypeEnum.SHOP_CATEGORY_PAGE, PageTypeEnum.SHOP_BOTTOM_NAVIGATION_PAGE);
        //校验是否包含所有必须的页面
        Set<Object> paramPageTypes = pageCountMap.keySet();
        if (!paramPageTypes.containsAll(requiredPage)) {
            throw ShopError.FAILED_TO_VALIDATE_TEMPLATE_PARAMETERS.dataEx("required page error");
        }
        //除自定义页面外 每个页面只能有一个
        pageCountMap.forEach(
                (pageType, pageCount) -> {
                    if (PageTypeEnum.SHOP_CUSTOMIZED_PAGE == pageType) {
                        return;
                    }
                    if (pageCount > 1) {
                        throw ShopError.FAILED_TO_VALIDATE_TEMPLATE_PARAMETERS.dataEx("page count error: " + pageType + " " + pageCount);
                    }
                }
        );

    }
}
