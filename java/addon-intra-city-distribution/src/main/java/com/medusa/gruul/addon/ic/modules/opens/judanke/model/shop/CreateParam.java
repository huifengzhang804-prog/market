package com.medusa.gruul.addon.ic.modules.opens.judanke.model.shop;

import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.GoodsTypeId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CreateParam implements Serializable {

    /**
     * 店铺名称
     */
    private String name;

    /**
     * phone	string	是
     * 店铺联系电话
     */
    private String phone;

    /**
     * province	string	是
     * 店铺省
     */
    private String province;

    /**
     * city	string	是
     * 店铺市
     */
    private String city;

    /**
     * district	string	是
     * 店铺区
     */
    private String district;

    /**
     * address	string	是
     * 店铺地址
     */
    private String address;

    /**
     * house_num	string	否
     * 店铺门牌号
     */
    private String houseNum;

    /**
     * location_poi	string	否
     * 店铺地址 POI
     */
    private String locationPoi;

    /**
     * lng	string	是
     * 经度（高德经纬度）
     */
    private BigDecimal lng;

    /**
     * lat	string	是
     * 维度（高德经纬度）
     */
    private BigDecimal lat;

    /**
     * goods_type_id	int	是
     * 物品类型id（2=快餐美食，13=水果生鲜，4=日用百货，5=蛋糕甜品，6=鲜花绿植，1=文件证件，7=手机数码，14=龙虾烧烤，15=火锅串串，16=成人用品，11=医药健康，12=其他）
     */
    private GoodsTypeId goodsTypeId;

    /**
     * contact_person	string	是
     * 联系人名称
     */
    private String contactPerson;

    /**
     * relative_user_id	string	是
     * 绑定商户ID
     */
    private Long relativeUserId;
}
