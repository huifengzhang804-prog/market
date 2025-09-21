package com.medusa.gruul.addon.supplier.mp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2023/7/21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("t_supplier_info")
public class SupplierInfo {

    /**
     * 供应商 id
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 供应商名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 供应商是否可用
     */
    private Boolean enabled;

}
