package com.medusa.gruul.addon.platform.model.dto;

import com.medusa.gruul.addon.platform.model.enums.PlatformError;
import com.medusa.gruul.addon.platform.model.enums.TemplateTypeEnum;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;

/**
 * <p>装修页面creat dto</p>
 *
 * @author An.Yan
 */
@Data
@Accessors(chain = true)
public class DecorationPageCreateDTO implements BaseDTO {

    @Serial
    private static final long serialVersionUID = -3762448023648263959L;

    /**
     * 页面名称
     */
    @NotNull
    @Length(min = 1, max = 10)
    private String name;

    /**
     * 页面说明
     */
    private String remark;

    /**
     * 页面json
     */
    private String properties;

    /**
     * 页面类型枚举
     */
    @NotNull
    private PageTypeEnum type;

    /**
     * 自定义类型，自定义页面时使用 和 type组合可以灵活使用
     */
    @Size(max = 20)
    private String customType;

    /**
     * 页面所属的模版的类型
     */
    @NotNull
    private TemplateTypeEnum templateType;

    /**
     * 仅当 templateTyp是平台时 该字段为NULL
     */
    private TemplateBusinessTypeEnum businessType;

    /**
     * 页面终端类型
     */
    @NotNull
    private DecorationEndpointTypeEnum endpointType;


    @Override
    public void validParam() {
        TemplateTypeEnum templateType = this.getTemplateType();
        // 根据模板类型检查允许的页面类型
        if (TemplateTypeEnum.PLATFORM == templateType) {
            if (type.isPlatform()) {
                setBusinessType(null);
                return;
            }
            throw PlatformError.PAGE_TYPE_INVALID.dataEx(
                    "page type invalid: " + type + " templateType: " + templateType);
        }
        //店铺页面必须有业务类型
        TemplateBusinessTypeEnum businessType = getBusinessType();
        if (businessType == null) {
            throw SystemCode.PARAM_VALID_ERROR.dataEx("businessType cannot be null");
        }
        //如果是 O2O 则只能新增 店铺首页
        if (businessType == TemplateBusinessTypeEnum.O2O) {
            if (PageTypeEnum.SHOP_HOME_PAGE == type) {
                return;
            }
            throw PlatformError.PAGE_TYPE_INVALID.dataEx("O2O only support SHOP_HOME_PAGE");
        }
        //如果是其它业务类型则只能新增店铺类型的页面
        if (!type.isPlatform()) {
            return;
        }
        throw PlatformError.PAGE_TYPE_INVALID.dataEx("page type invalid: " + type + " businessType: " + businessType);

    }
}
