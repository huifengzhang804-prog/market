package com.medusa.gruul.search.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.search.api.enums.OperationType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.List;

/**
 * @author xiaoq
 * @since 2023-12-08 14:30
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_search_user_action", autoResultMap = true)
public class SearchUserAction extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 用户操作内容
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<OperationType> operation;


}
