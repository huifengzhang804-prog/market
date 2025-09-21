package com.medusa.gruul.overview.service.model.dto;

import com.medusa.gruul.common.web.valid.annotation.Price;
import com.medusa.gruul.overview.api.enums.OwnerType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/11/29
 */
@Getter
@Setter
@ToString
public class ShopWithdrawDTO implements Serializable {

    @NotNull
    @Price
    @Min(100)
    private Long amount;

    private OwnerType type;
}
