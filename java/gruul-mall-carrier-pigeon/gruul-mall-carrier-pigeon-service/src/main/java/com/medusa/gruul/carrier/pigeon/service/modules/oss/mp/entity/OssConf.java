package com.medusa.gruul.carrier.pigeon.service.modules.oss.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.enums.StorageType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * <p>
 * 系统配置
 * </p>
 *
 * @author whh
 * @since 2019-09-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_oss_conf")
public class OssConf extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 类型
     */
    private StorageType type;
    /**
     * 说明
     */
    @TableField("description")
    private String  description;

    /**
     * 对应类型的 OSS 配置
     */
    @TableField("`conf`")
    private String conf;

    /**
     * 是否启用
     */
    @TableField( exist = false)
    private Boolean using;

}
