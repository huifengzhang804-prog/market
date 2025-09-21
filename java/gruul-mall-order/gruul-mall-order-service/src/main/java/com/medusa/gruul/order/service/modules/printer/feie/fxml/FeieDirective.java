package com.medusa.gruul.order.service.modules.printer.feie.fxml;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/14
 */
@Getter
@RequiredArgsConstructor
public enum FeieDirective {
    //空指令直接展示内容
    NONE(false, null),
    //<BR> ：换行符
    BR(true, "BR"),
    //<CUT> ：切刀指令(主动切纸,仅限切刀打印机使用才有效果)
    CUT(true, "CUT"),
    //<LOGO> ：打印LOGO指令(前提是预先在机器内置LOGO图片)
    LOGO(true, "LOGO"),
    //<PLUGIN> ：钱箱或者外置音响指令
    PLUGIN(true, "PLUGIN"),
    //<CB></CB> ：居中放大
    CB(false, "CB"),
    //<B></B> ：放大一倍
    B(false, "B"),
    // <DB></DB> 放大两倍
    // DB(false, "DB"),
    //<C></C> ：居中
    C(false, "C"),
    //<L></L> ：字体变高一倍
    L(false, "L"),
    //<W></W> ：字体变宽一倍
    W(false, "W"),
    //<QR></QR> ：二维码（单个订单，最多只能打印一个二维码）
    QR(false, "QR"),
    //<RIGHT></RIGHT> ：右对齐
    RIGHT(false, "RIGHT"),
    //<BOLD></BOLD> ：字体加粗
    BOLD(false, "BOLD"),
    //说明：来订单时默认播放新来单语音，若使用“申请退单”或“申请取消订单”的语音，请使用以下指令
    //<AUDIO-REFUND> ：申请退单语音指令。播报内容为：有用户申请退单了
    AUDIO_REFUND(true, "AUDIO-REFUND"),
    //<AUDIO-CANCEL> ：申请取消订单语音指令。播报内容为：有用户申请取消订单了
    AUDIO_CANCEL(true, "AUDIO-CANCEL"),
    //说明：条形码标签仅支持以下标签规定的内容打印，如需要打印其它特殊字符条形码，需要点击后面的 飞鹅云条形码函数按钮
    //下载条形码函数进行调用打印
    //<BC128_A>123ABCDEF</BC128_A> ：数字和大写字母混合的条形码，最多支持14位的数字、大写字母混合条形码
    BC128_A(false, "BC128_A"),
    //<BC128_B>123ABCDef</BC128_B> ：数字和大小写字母混合的条形码，最多支持14位的数字、大写字母、小写字母混合条形码
    BC128_B(false, "BC128_B"),
    //<BC128_C>0123456789</BC128_C> ：最多支持22位纯数字
    BC128_C(false, "BC128_C");

    /**
     * 是否时单标签
     */
    private final boolean single;

    /**
     * 标签
     */
    private final String label;


    /**
     * 带内容渲染标签
     *
     * @param content 标签包裹的内容
     * @return 带内容渲染出的标签
     */
    public String render(String content) {
        String labelEnd = label + ">";
        if (single) {
            return "<" + labelEnd;
        }
        content = content == null ? StrUtil.EMPTY : content;
        if (label == null || label.isEmpty()) {
            return content;
        }
        return "<" + labelEnd + content + "</" + labelEnd;
    }

    /**
     * 空内容标签
     *
     * @return 空内容标签
     */
    public String render() {
        return render(StrUtil.EMPTY);
    }

}
