package com.medusa.gruul.order.service.modules.printer.feie.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 打印小票参数
 *
 * @author 张治保
 * @since 2024/8/14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PrintMsgParam {

    /**
     * backurl 		string	必须先在管理后台设置，回调数据格式详见《订单状态回调》
     */
    private String backurl;

    /**
     * expired 		int	订单失效UNIX时间戳，10位，精确到秒，打印时超过该时间该订单将抛弃不打印，取值范围为：当前时间<订单失效时间≤24小时后。
     */
    private Long expired;

    /**
     * sn(必填)	string	打印机编号
     */
    private String sn;

    /**
     * content(必填) 	必须	string	打印内容,不能超过5000字节
     */
    private String content;

    /**
     * times 		int	打印次数，默认为1。
     */
    private Integer times;


}
