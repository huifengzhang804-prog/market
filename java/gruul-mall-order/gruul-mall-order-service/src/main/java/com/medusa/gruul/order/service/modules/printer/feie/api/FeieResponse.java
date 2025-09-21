package com.medusa.gruul.order.service.modules.printer.feie.api;

import com.medusa.gruul.global.model.helper.request.IResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 飞鹅 公共响应参数
 *
 * @author 张治保
 * @since 2024/8/14
 */
@Getter
@Setter
@ToString
public class FeieResponse<T> implements IResponse<T> {

    /**
     * ret 	int	返回码，正确返回0，【注意：结果正确与否的判断请用此返回参数】 ，错误返回非零
     */
    private Integer ret;

    /**
     * msg 	string	结果提示信息，正确返回”ok”，如果有错误，返回错误信息。
     */
    private String msg;

    /**
     * data 	any	数据类型和内容详看私有返回参数data，如果有错误，返回null。
     */
    private T data;

    /**
     * serverExecutedTime 	int	服务器程序执行时间，单位：毫秒。
     */
    private Long serverExecutedTime;

    @Override
    public boolean isSuccess() {
        return ret != null && ret == 0;
    }
}
