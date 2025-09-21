package com.medusa.gruul.afs.service.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * @author 张治保
 * date 2022/10/22
 */
@Getter
@Setter
@ToString
public class AfsRemarkDTO {

    /**
     * 售后工单号列表
     */
    private Set<String> nos;

    /**
     * 备注详情
     */
    private String remark;
}
