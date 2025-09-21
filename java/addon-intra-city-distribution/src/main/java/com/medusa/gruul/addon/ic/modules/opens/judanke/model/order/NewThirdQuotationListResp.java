package com.medusa.gruul.addon.ic.modules.opens.judanke.model.order;

import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.DeliveryBrand;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.Switch;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.DeliveryType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/7
 */
@Getter
@Setter
@ToString
public class NewThirdQuotationListResp {

    /**
     * type	int	是
     * 运力类型（0=聚合运力，1=绑定运力）
     */
    private DeliveryType type;

    /**
     * brand	string	是
     * 运力品牌（ddks=达达、fnpt=蜂鸟、kfw=快服务、mtpt=美团配送、sftc=顺丰同城、ss=闪送、uupt=UU跑腿、gxd=裹小递、apt=爱跑腿、blpt=帮啦跑腿、ccs=曹操送、kpz=kpz、ldpt=来答跑腿、hlks=哈啰快送、mtzb=美团跑腿、jfkp=飓风快跑、fnzb=蜂鸟众包）
     */
    private DeliveryBrand brand;

    /**
     * close	int	是
     * 是否不可用 1不可用 2 可用
     */
    private Switch close;

    /**
     * message	string	是
     * 关闭不可用原因
     */
    private String message;

    /**
     * distance	int	是
     * 距离（单位：米）
     */
    private Long distance;

    /**
     * selected	int	是
     * 在追加运力询价时候，之前是否已经选中过该品牌，如果有的话，该品牌所有类型均为选中状态（0=未选中，1=已选）
     */
    private Switch selected;

    /**
     * total_fee	int	是
     * 运费金额（单位：分）
     */
    private Long totalFee;
}
