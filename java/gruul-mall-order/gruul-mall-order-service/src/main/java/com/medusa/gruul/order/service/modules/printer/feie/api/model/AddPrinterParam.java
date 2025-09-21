package com.medusa.gruul.order.service.modules.printer.feie.api.model;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 打印机编号SN(必填) # 打印机识别码KEY(必填) # 备注名称(选填) # 流量卡号码(选填)，
 * 多台打印机请换行（\n）添加新打印机信息，每次最多100行(台)。
 *
 * @author 张治保
 * @since 2024/8/14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AddPrinterParam {

    /**
     * 分隔符
     */
    private static final String CONJUNCTION = "#";


    /**
     * 打印机编号SN(必填)
     */
    private String sn;

    /**
     * 打印机识别码KEY(必填)
     */
    private String key;

    /**
     * 备注名称(选填)
     */
    private String name;

    /**
     * 流量卡号码(选填)
     */
    private String flowCard;

    /**
     * 批量格式化
     * 多台打印机请换行（\n）添加新打印机信息，每次最多100行(台)。
     *
     * @param printers 打印机配置列表
     * @return 批量格式化结果
     */
    public static String batchFormat(List<AddPrinterParam> printers) {
        if (printers == null || printers.isEmpty()) {
            return StrUtil.EMPTY;
        }
        //每次最多100行(台)。
        if (printers.size() > 100) {
            throw new IllegalArgumentException("printers size cannot greater than 100");
        }
        return printers.stream()
                .map(AddPrinterParam::format)
                .collect(Collectors.joining(StrPool.LF));
    }

    /**
     * 格式化为 飞鹅需要的数据格式
     * 打印机编号SN(必填) # 打印机识别码KEY(必填) # 备注名称(选填) # 流量卡号码(选填)
     * eg. 316500010 # abcdefgh # 快餐前台 # 13688889999
     *
     * @return 飞鹅需要的数据格式
     */
    public String format() {
        return sn + CONJUNCTION + key + (name == null ? StrUtil.EMPTY : CONJUNCTION + name) + (flowCard == null ? StrUtil.EMPTY : CONJUNCTION + flowCard);
    }
}
