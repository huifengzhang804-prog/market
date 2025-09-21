package com.medusa.gruul.addon.platform.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description: 平台类目排序dto
 * @Author: xiaoq
 * @Date : 2022-04-22 19:05
 */
@Data
public class PlatformCategorySortDTO {
    private  Long parentId;

    /**
     * 平台类目ids
     */
    private List<Long> sortedIds;
}
