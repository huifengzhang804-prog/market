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
public class UnbindParam {
    /**
     * guid	string	是
     * 打印机列表中guid
     */
    private String guid;

    /**
     * relative_user_id	int	是
     * 关联的商户id
     */
    private Long relativeUserId;
}
