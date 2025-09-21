package com.medusa.gruul.service.uaa.service.model.dto.wechat;

import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * 小程序码DTO
 *
 * @author xiaoq
 * @Description WeChatCodeDTO.java
 * @date 2022-12-02 16:04
 */
@Data
public class WeChatCodeDTO {

    /**
     * 扫码进入的小程序页面路径，最大长度 128 字节，不能为空,可页面参数,请求头需带上shopId
     */
    @NotBlank(message = "路径不能为空")
    private String path;

    /**
     * 默认"release" 要打开的小程序版本。正式版为 "release"，体验版为 "trial"，开发版为 "develop"
     */
    private String envVersion = "release";

    /**
     * 二维码的宽度，单位 px。最小 280px，最大 1280px,非必填,默认430px
     */
    private Integer width = 430;

    /**
     * 默认true 自动配置线条颜色，如果颜色依然是黑色，则说明不建议配置主色调
     */
    private Boolean autoColor = Boolean.TRUE;

    /**
     * autoColor 为 false 时生效，使用 rgb 设置颜色 例如 {"r":"xxx","g":"xxx","b":"xxx"}
     */
    private WxMaCodeLineColor lineColor;

    /**
     * 是否需要透明底色
     */
    private Boolean isHyaline = Boolean.FALSE;


}
