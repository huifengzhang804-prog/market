package com.medusa.gruul.order.service.modules.printer.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.order.api.enums.PrintLink;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrinterBrand;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintTemplateConfig;
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
@TableName(value = "t_print_template", autoResultMap = true)
public class PrintTemplate extends BaseEntity {

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
     * 打印类型  某联
     */
    private PrintLink link;

    /**
     * 打印纸宽
     */
    private FeieTicketSize size;

    /**
     * 原始模板配置
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private PrintTemplateConfig config;

    /**
     * 原始模板配置对应的模板
     */
    private String template;

}
