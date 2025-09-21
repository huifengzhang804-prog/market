package com.medusa.gruul.addon.live.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.live.enums.CategoryType;
import com.medusa.gruul.addon.live.enums.ProhibitedType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author miskw
 * @date 2023/6/9
 * @describe 描述
 */
@Getter
@Setter
public class UpdateAnchorDTO implements BaseDTO {
    /**
     * 主播id
     */
    @NotNull(message = "来源id不能为空")
    private Long sourceId;
    /**
     * 来源类型
     */
    @NotNull(message = "来源类型不能为空")
    private ProhibitedType type;
    /**
     * 操作类型 启动|或禁用
     */
    @NotNull(message = "对主播的操作不能为空")
    private Boolean isEnable;
    /**
     * 禁播类型
     */
    private List<CategoryType> categoryTypes;
    /**
     * 禁播原因
     */
    private String reason;
    /**
     * 相关证据
     */
    private String relevantEvidence;

    @Override
    public void validParam() {
        if (!isEnable) {
            if (CollUtil.isEmpty(categoryTypes) || StrUtil.isEmpty(reason) || StrUtil.isEmpty(relevantEvidence)) {
                throw new GlobalException(SystemCode.REQ_REJECT_CODE, "参数校验失败");
            }
        }
    }
}
