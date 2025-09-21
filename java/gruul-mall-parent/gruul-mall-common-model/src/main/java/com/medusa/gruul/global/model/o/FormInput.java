package com.medusa.gruul.global.model.o;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.global.model.enums.InputType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <code>{"key":"手机号","type":"MOBILE","required":true,"placeholder":"请输入您的手机号码"}</code>
 *
 * @author 张治保
 * date 2022/10/26
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class FormInput implements Serializable {

    /**
     * 表单名称
     */
    @NotBlank
    @Size(max = 8)
    private String key;

    /**
     * 表单类型
     */
    @NotNull
    private InputType type;

    /**
     * 是否必须
     */
    @NotNull
    private Boolean required;

    /**
     * 占位符
     */
    @Size(max = 50)
    private String placeholder;

    /**
     * 校验输入内容是否匹配当前表单
     *
     * @param inputValue 输入内容
     * @return true 匹配
     */
    public final boolean valid(String inputValue) {
        //是否必填
        boolean isRequired = BooleanUtil.isTrue(required);
        //
        boolean isBlankInput = StrUtil.isBlank(inputValue);
        //如果是必填且输入为空，返回false
        if (isRequired && isBlankInput) {
            return false;
        }
        //如果 输入内容不为空，校验输入是否匹配
        if (!isBlankInput) {
            return type.matches(inputValue);
        }
        return true;
    }

}
