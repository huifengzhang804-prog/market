package com.medusa.gruul.service.uaa.service.model.dto.wechat;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/6/10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class WechatSchemaDTO implements Serializable {

    /**
     * 通过scheme码进入的小程序页面路径，必须是已经发布的小程序存在的页面，不可携带query。path为空字符串时会跳转小程序主页。
     * <pre>
     * 是否必填：是
     * </pre>
     */
    @NotNull
    private String path;

    /**
     * 通过scheme码进入的小程序页面参数，key-value格式，query参数可为空。
     */
    private JSONObject query = new JSONObject();


    /**
     * 要打开的小程序版本。正式版为"release"，体验版为"trial"，开发版为"develop"默认值：release
     */
    private String envVersion = "release";

    /**
     * 失效时间 单位 天 最长30天 默认1 天
     */
    private Integer expireDays = 1;


}
