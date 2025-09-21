package com.medusa.gruul.addon.ic.modules.opens.judanke.model.order;

import com.alibaba.fastjson2.annotation.JSONField;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.DeliveryBrand;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.GoodsTypeId;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.Switch;
import com.medusa.gruul.common.fastjson2.FastJson2;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/8/7
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderCreateParam {

    /**
     * third_order_no	string	是
     * 商户订单号
     */
    private String thirdOrderNo;

    /**
     * booking	int	否
     * 是否为预定单。0:否,1:是
     */
    private Switch booking;

    /**
     * pickup_time	string	否
     * 预定取件时间（booking=1有效）yyyy-MM-dd HH:mm:ss
     */
    @JSONField(format = FastJson2.DATETIME_PATTEN)
    private LocalDateTime pickupTime;

    /**
     * reserve_arrival_time	string	否
     * 期望送达时间 不传为立即送达
     */
    @JSONField(format = FastJson2.DATETIME_PATTEN)
    private LocalDateTime reserveArrivalTime;

    /**
     * shop_id	int	否
     * 店铺id（和发件人信息必选一项）
     */
    private Long shopId;

    /**
     * sender_name	string	否
     * 发件人名字（发件人信息和店铺id必选一项，如选择个人发单时，发件人默认店铺信息为快宝网络，建议创建店铺发单）
     */
    private String senderName;

    /**
     * sender_phone	string	否
     * 发件人电话
     */
    private String senderPhone;

    /**
     * sender_province	string	否
     * 发件人省（未传会根据经纬度进行解析）
     */
    private String senderProvince;

    /**
     * sender_city	string	否
     * 发件人市（未传会根据经纬度进行解析）
     */
    private String senderCity;

    /**
     * sender_district	string	否
     * 发件人区/县（未传会根据经纬度进行解析）
     */
    private String senderDistrict;

    /**
     * sender_address	string	否
     * 发件人地址（请不要包含省市区信息）
     */
    private String senderAddress;

    /**
     * sender_house_num	string	否
     * 发件人门牌号
     */
    private String senderHouseNum;

    /**
     * sender_location_poi	string	否
     * 发件人地址 POI
     */
    private String senderLocationPoi;

    /**
     * sender_lng	string	否
     * 发件人经度（高德经纬度）
     */
    private BigDecimal senderLng;

    /**
     * sender_lat	string	否
     * 发件人维度（高德经纬度）
     */
    private BigDecimal senderLat;

    /**
     * receiver_name	string	是
     * 收件人名字
     */
    private String receiverName;

    /**
     * receiver_phone	string	是
     * 收件人电话
     */
    private String receiverPhone;

    /**
     * receiver_province	string	否
     * 收件人省（未传会根据经纬度进行解析）
     */
    private String receiverProvince;

    /**
     * receiver_city	string	否
     * 收件人市（未传会根据经纬度进行解析）
     */
    private String receiverCity;

    /**
     * receiver_district	string	否
     * 收件人区/县（未传会根据经纬度进行解析）
     */
    private String receiverDistrict;

    /**
     * receiver_address	string	是
     * 收件人地址（请不要包含省市区信息）
     */
    private String receiverAddress;

    /**
     * receiver_house_num	string	否
     * 收件人门牌号
     */
    private String receiverHouseNum;

    /**
     * receiver_location_poi	string	否
     * 收件人地址 POI
     */
    private String receiverLocationPoi;

    /**
     * receiver_lng	string	是
     * 收件人经度（高德经纬度）
     */
    private BigDecimal receiverLng;

    /**
     * receiver_lat	string	是
     * 收件人维度（高德经纬度）
     */
    private BigDecimal receiverLat;

    /**
     * goods_price	string	是
     * 商品价格（单位：分）
     */
    private BigDecimal goodsPrice;

    /**
     * goods_weight	int	是
     * 商品重量（单位：克）
     */
    private Long goodsWeight;

    /**
     * goods_quantity	int	是
     * 商品数量
     */
    private Long goodsQuantity;

    /**
     * goods_type_id	int	 否
     * 个人发单必传，店铺发单取店铺物品类型
     * 物品类型id（2=快餐美食，13=水果生鲜，4=日用百货，5=蛋糕甜品，6=鲜花绿植，1=文件证件，7=手机数码，14=龙虾烧烤，15=火锅串串，16=成人用品，11=医药健康，12=其他）
     */
    private GoodsTypeId goodsTypeId;

    /**
     * delivery_remark	string	否
     * 发单备注（传给运力公司，例如：期望 17:25 前送达）
     */
    private String deliveryRemark;

    /**
     * delivery_brands	array	否 (注：创建并发单需要传、 预创建订单不需要传)
     * 选取运力匹配
     */
    private Set<DeliveryBrand> deliveryBrands;

    /**
     * tip_fee	int	否
     * 小费（单位：分，以 100 为递增）
     */
    private Long tipFee;

    /**
     * relative_user_id	string	是
     * 聚单客商家ID
     */
    private Long relativeUserId;

    /**
     * third_platform	string	否
     * 三方平台标识，如果不在枚举支持自定义，最大长度6（例如meituan，wm=微盟，yz=有赞，eb=饿百，dyxd=抖音小店，ele=饿了么外卖，jddj=京东到家，mtsg=美团闪购，meituan=美团外卖，weidian=微店）
     */
    private String thirdPlatform;

    /**
     * third_day_seq	int	否
     * 三方日序号（例如美团6号单 传 6）
     */
    private Long thirdDaySeq;

    /**
     * max_delivery_price	int	否
     * 最大限定配送金额（单位：分，不包含小费，如果配送费超过这个金额将停止发出。备注：多个情况下，只有比限定价格高的运力会停止发出，如果某个品牌比限定价格低正常发出，如果都不能使用将会停止）
     */
    private Long maxDeliveryPrice;
}
