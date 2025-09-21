package com.medusa.gruul.addon.ic.modules.opens.judanke.model.printer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PrintsResp {
    /**
     * status	string	是
     * 打印任务提交结果
     */

    private String status;

    /**
     * task_id	string	是
     * 打印任务ID
     */
    private String taskId;


}
