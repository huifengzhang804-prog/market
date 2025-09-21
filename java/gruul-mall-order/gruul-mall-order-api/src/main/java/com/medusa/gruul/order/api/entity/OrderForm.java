package com.medusa.gruul.order.api.entity;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.global.model.o.FormInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 下单 表单设置
 *
 * @author 张治保
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_order_form", autoResultMap = true)
public class OrderForm extends BaseEntity implements BaseDTO {
    /**
     * 商家注册信息id
     */
    private Long shopId;

    /**
     * 是否开启下单通知
     */
    @NotNull
    private Boolean orderNotify;

    /**
     * 自定义表单
     */
    @Valid
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<@Valid FormInput> customForm;


    @Override
    public void validParam() {
        if (CollUtil.isEmpty(customForm)) {
            return;
        }
        //表单名称不能重复
        Set<String> keys = customForm.stream()
                .map(FormInput::getKey)
                .collect(Collectors.toSet());
        if (keys.size() != customForm.size()) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
    }
}
