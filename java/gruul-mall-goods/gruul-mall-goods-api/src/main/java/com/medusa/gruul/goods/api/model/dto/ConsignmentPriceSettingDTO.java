package com.medusa.gruul.goods.api.model.dto;

import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.goods.api.model.enums.PricingType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author miskw
 * @date 2023/8/8
 * @describe 代销价格设置DTO
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ConsignmentPriceSettingDTO implements BaseDTO {
    private static final long RATE = 1000000;
    /**
     * 设价方式
     */
    private PricingType type;
    /**
     * 销售价比值
     */
    private Long sale;
    /**
     * 划线价比值
     */
    private Long scribe;

    @Override
    public void validParam() {
        if (type != null) {
            if (sale == null || scribe == null) {
                throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "代销商品比值不能为空");
            }
            if (type == PricingType.RATE) {
//                if (!(sale >= CommonPool.NUMBER_ZERO && sale <= CommonPool.UNIT_CONVERSION_TEN_THOUSAND * CommonPool.UNIT_CONVERSION_HUNDRED)) {
//                    throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "销售价数值超出限制");
//                }
                if (!(scribe >= CommonPool.NUMBER_ZERO && scribe <= CommonPool.UNIT_CONVERSION_TEN_THOUSAND * CommonPool.UNIT_CONVERSION_HUNDRED)) {
                    throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "划线价数值超出限制");
                }
            }

            if (type == PricingType.REGULAR) {
                if (!(sale >= CommonPool.NUMBER_ZERO && sale <= 9000 * CommonPool.UNIT_CONVERSION_TEN_THOUSAND)) {
                    throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "销售价数值超出限制");
                }
                if (!(scribe >= CommonPool.NUMBER_ZERO && scribe <= 9000 * CommonPool.UNIT_CONVERSION_TEN_THOUSAND)) {
                    throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "划线价数值超出限制");
                }
            }

        }
    }

    /**
     * 代销金额计算
     *
     * @param salesPrice sku原始销售价
     * @return 价格计算后的结果
     */
    public ConsignmentPriceDTO singlePrice(Long salesPrice) {
        boolean isRegular = getType() == PricingType.REGULAR;
        long newSalePrice = isRegular
                ? getSale()
                : (salesPrice * getSale() / RATE);
        newSalePrice = salesPrice + newSalePrice;

        long newPrice = isRegular
                ? getScribe()
                : (newSalePrice * getScribe() / RATE);
        newPrice = newSalePrice + newPrice;
        return new ConsignmentPriceDTO(
                priceRound(newSalePrice),
                priceRound(newPrice)
        );
    }

    /**
     * 百位取整
     */
    private long priceRound(long price) {
        return (price / 100) * 100;
    }


}
