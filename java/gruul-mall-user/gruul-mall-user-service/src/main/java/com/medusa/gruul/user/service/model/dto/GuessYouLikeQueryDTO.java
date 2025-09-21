package com.medusa.gruul.user.service.model.dto;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.user.api.model.GuessYouLikeVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author WuDi
 */
@Getter
@Setter
@ToString
public class GuessYouLikeQueryDTO extends Page<GuessYouLikeVO> {

}
