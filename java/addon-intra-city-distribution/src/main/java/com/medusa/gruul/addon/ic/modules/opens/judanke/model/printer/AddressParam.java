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
public class AddressParam {
    /**
     * province	string	是
     * 省
     */
    private String province;

    /**
     * city	string	是
     * 市
     */
    private String city;

    /**
     * district	string	是
     * 区
     */
    private String district;

    /**
     * detail	string	是
     * 详细地址
     */
    private String detail;

    /**
     * town	string	是
     * 乡镇街道
     */
    private String town;
}
