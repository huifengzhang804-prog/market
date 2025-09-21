package com.medusa.gruul.order.service.modules.printer.mq;

import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.order.service.modules.printer.feie.api.FeieResponse;
import com.medusa.gruul.order.service.modules.printer.feie.api.IFeieApi;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.PrintMsgParam;
import com.medusa.gruul.order.service.modules.printer.model.bo.PrintPublishBO;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintFormat;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author 张治保
 * @since 2024/11/19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PrintRabbitListener {

    private static final String PRINT_LOCK_KEY = "order:print_lock_key";

    private final IFeieApi feieApi;

    /**
     * 打印队列消费
     *
     * @param param   打印参数
     * @param channel 通道
     * @param message 消息
     * @throws IOException 异常
     */
    @RabbitListener(queues = PrintRabbitQueueNames.DO_PRINT_QUEUE)
    public void printRabbitQueue(PrintPublishBO param, Channel channel, Message message) throws IOException {
        //针对打印机加锁避免 同时被多个订单同时打印
        print(param);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 执行打印
     *
     * @param param 打印参数
     */
    private void print(PrintPublishBO param) {
        String fxml = param.getContent();
        //如果 打印内容没被切割  直接打印
        String printerSn = param.getSn();
        //打印次数
        Integer times = param.getTimes();
        List<String> contents = PrintFormat.printContentSplit(fxml);
        if (contents.size() == 1) {
            log.debug("打印内容：\n{}", fxml);
            //调用飞鹅打印机接口打印
            toPrint(printerSn, fxml, times == null ? CommonPool.NUMBER_ONE : times);
            return;
        }
        // 打印内容被切割  循环打印
        log.debug("打印内容超出5000字节，开始循环打印。。。。。\n原始内容：{}\n切割内容：{}", fxml, contents);
        //针对打印机sn编码加锁
        // 加锁 避免打印未完成时 被其他订单打印 导致排版错乱
        RedisUtil.lockRun(
                RedisUtil.key(PRINT_LOCK_KEY, printerSn),
                () -> {
                    if (times == null || times == 1) {
                        for (String content : contents) {
                            log.debug("打印内容：\n{}", content);
                            toPrint(printerSn, content, CommonPool.NUMBER_ONE);
                        }
                        return;
                    }

                    for (int time = 1; time <= times; time++) {
                        for (String content : contents) {
                            log.debug("{}-打印内容：\n{}", time, content);
                            toPrint(printerSn, content, CommonPool.NUMBER_ONE);
                        }
                    }
                }
        );
    }

    /**
     * 调用飞鹅打印机接口打印
     *
     * @param sn    打印机sn编号
     * @param fxml  打印内容
     * @param times 打印次数
     */
    private void toPrint(String sn, String fxml, int times) {
        FeieResponse<String> response = feieApi.printMsg(
                new PrintMsgParam()
                        .setSn(sn)
                        .setContent(fxml)
                        //不传 默认为 1
                        .setTimes(times <= CommonPool.NUMBER_ONE ? null : times)
        );
        if (!response.isSuccess()) {
            log.error("打印失败：{}", response);
        }
    }
}
