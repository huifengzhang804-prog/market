package com.medusa.gruul.common.idem.model;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/6/16
 */
@Getter
@RequiredArgsConstructor
public enum IdemError implements Error {
    /**
     * 重复提交不生效
     */
    REPEAT_SUBMIT(1001, "");

    private final int code;

    private final String msgCode;

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return I18N.msg("idem.repeat.submit");
    }
}
