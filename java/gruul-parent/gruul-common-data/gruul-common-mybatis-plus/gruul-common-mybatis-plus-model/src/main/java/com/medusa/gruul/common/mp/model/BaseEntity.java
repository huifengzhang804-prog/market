package com.medusa.gruul.common.mp.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * @author 张治保
 * date 2022/2/17
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Accessors(chain = true)
public class BaseEntity extends com.medusa.gruul.common.mp.model.base.BaseEntity {
    @Serial
    private static final long serialVersionUID = -5441749920695254908L;
    /**
     * id
     * {@link com.medusa.gruul.common.mp.config.MybatisPlusConfig#identifierGenerator()}
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 乐观锁版本号
     */
    @Version
    @TableField(value = "version", fill = FieldFill.INSERT)
    private Integer version;
    /**
     * 逻辑删除标记
     */
    @TableLogic(value = "0", delval = "1")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Boolean deleted;
}
