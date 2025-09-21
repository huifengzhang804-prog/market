package com.medusa.gruul.addon.bargain.model.dto;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.bargain.model.vo.BargainHelpPeopleVO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author wudi
 */
@Getter
@Setter
@ToString
public class BargainHelpPeopleQueryDTO extends Page<BargainHelpPeopleVO> {


    /**
     * 活动id
     */
    @NotNull
    private Long activityId;

    /**
     * 砍价订单id
     */
    @NotNull
    private Long bargainOrderId;


}
