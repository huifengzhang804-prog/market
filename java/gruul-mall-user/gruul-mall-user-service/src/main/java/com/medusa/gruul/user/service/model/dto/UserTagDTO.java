package com.medusa.gruul.user.service.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 会员标签
 *
 * @author: WuDi
 * @date: 2022/9/15
 */
@Data
public class UserTagDTO {

    /**
     * 添加标签
     */
    @Valid
    private List<TagDTO> addTagList;

    /**
     * 修改标签
     */
    @Valid
    private List<TagDTO> updateTagList;

    /**
     * 删除标签
     */
    @Valid
    private List<Long> delUserTagIdList;


    /**
     * 用户id
     */
    @Size(min = 1)
    private Set<Long> userIdList;


    @Data
    public static class TagDTO {
        /**
         * 会员标签id
         */
        private Long id;
        /**
         * 会员标签名称
         */
        @Size(min = 1, max = 10, message = "标签名称长度在1-10之间")
        private String tagName;
        /**
         * 是否选中true选中,false未选中
         */
        private Boolean option;
    }

}
