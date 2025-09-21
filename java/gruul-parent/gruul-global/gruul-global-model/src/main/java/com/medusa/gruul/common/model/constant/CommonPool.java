package com.medusa.gruul.common.model.constant;

import java.math.BigDecimal;

/**
 * @author xiaoq
 * @date 2022/02/19
 * Common常量池
 */
public interface CommonPool {
    /****************  BigDecimal  *****************/
    BigDecimal MIN = new BigDecimal("0.01");
    /****************  数字常量  *****************/
    int NUMBER_ZERO = 0;
    int NUMBER_ONE = 1;
    int NUMBER_TWO = 2;
    int NUMBER_THREE = 3;
    int NUMBER_FOUR = 4;
    int NUMBER_FIVE = 5;
    int NUMBER_SIX = 6;
    int NUMBER_SEVEN = 7;
    int NUMBER_EIGHT = 8;
    int NUMBER_NINE = 9;
    int NUMBER_TEN = 10;
    int NUMBER_TWELVE = 12;
    int NUMBER_FIFTEEN = 15;
    int NUMBER_TWENTY_EIGHT = 28;
    int NUMBER_THIRTY = 30;
    int NUMBER_NINETY = 90;
    int NUMBER_ONE_HUNDRED = 100;

    int NUMBER_ONE_HUNDRED_TWENTY = 120;

    int ONE_THOUSAND_TWENTY_FOUR = 1024;

    /**
     * @deprecated 请使用 {@link SqlHelper#SQL_LIMIT_1}
     */
    @Deprecated
    String SQL_LIMIT_1 = "LIMIT 1";

    /************** String常量*******************/
    String CODE = "code";

    /**
     * 手机区号
     */
    String NATION_CODE = "86";


    /************** 单位转换常量 *********************/
    Long UNIT_CONVERSION_TEN_THOUSAND = 10000L;

    Long UNIT_CONVERSION_HUNDRED = 100L;


    BigDecimal BIG_DECIMAL_UNIT_CONVERSION_TEN_THOUSAND = new BigDecimal("10000");

    BigDecimal BIG_DECIMAL_UNIT_CONVERSION_HUNDRED = new BigDecimal("100");

}
