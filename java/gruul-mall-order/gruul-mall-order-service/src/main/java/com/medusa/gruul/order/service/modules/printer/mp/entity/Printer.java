package com.medusa.gruul.order.service.modules.printer.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrinterBrand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("t_printer")
public class Printer extends BaseEntity {
    /**
     * 所属店铺
     */
    private Long shopId;

    /**
     * 关联的业务模式
     */
    @TableField("`mode`")
    private PrintMode mode;

    /**
     * 模板名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 打印机品牌
     */
    private PrinterBrand brand;

    /**
     * 使用位置
     */
    private String place;

    /**
     * 打印机 sn ｜ siid 编码
     */
    private String sn;

    /**
     * 打印机 key 可为空
     */
    @TableField("`key`")
    private String key;

    /**
     * 打印机适用的纸张尺寸 通过打印机信息查询
     */
    @TableField("`size`")
    private FeieTicketSize size;

    /**
     * 流量卡号码  流量卡版本的打印机必填
     */
    private String flowCard;
}
