package com.medusa.gruul.afs.service.service;

import com.medusa.gruul.afs.service.model.dto.AfsApplyDTO;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/3
 */
public interface AfsOrderApplyService {

    /**
     * 申请售后
     *
     * @param createAfs 创建信息
     *
     */
    void apply(AfsApplyDTO createAfs);
}
