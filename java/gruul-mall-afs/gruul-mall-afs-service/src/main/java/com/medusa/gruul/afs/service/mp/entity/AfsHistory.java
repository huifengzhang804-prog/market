package com.medusa.gruul.afs.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.afs.service.model.dto.BuyerReturnedDTO;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.order.api.enums.PackageStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.List;

/**
 * <p>
 * 售后历史
 * </p>
 *
 * @author 张治保
 * @since 2022-08-03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_afs_history", autoResultMap = true)
public class AfsHistory extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 售后工单号
     */
    private String afsNo;

    /**
     * 当前这条售后历史的处理方类型
     */
    private AfsStatus afsStatus;

    /**
     * 当前包裹状态
     */
    private PackageStatus packageStatus;

    /**
     * 描述
     */
    private String remark;

    /**
     * 证据视频/图片
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> evidences;


    /**
     * 退货信息
     */
    @TableField(exist = false)
    private BuyerReturnedDTO historyBuyerReturnedInfo;


}
