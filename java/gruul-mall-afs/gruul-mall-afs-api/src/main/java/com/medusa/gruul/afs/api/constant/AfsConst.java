package com.medusa.gruul.afs.api.constant;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/4
 */
public interface AfsConst {

    /**
     * 申请售后工单锁前缀
     */
    String AFS_APPLY_LOCK = "afs:apply:lock";

    /**
     * 售后工单 退款同意 锁
     */
    String AFS_AGREE_LOCK = "afs:agree:lock";
    
    /**
     * 店铺订单备注sql 模板
     */
    String AFS_ORDER_REMARK_SQL_TEMPLATE = "remark = CONCAT('({})',remark)";
}
