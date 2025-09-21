package com.medusa.gruul.payment.service;

import com.medusa.gruul.payment.api.model.transfer.AliTransferParam;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author jipeng
 * @since 2024/7/31
 */
@SpringBootTest
@Slf4j
public class AliPayTest {


  @Resource
  PaymentRpcService paymentRpcService;
    @Test
    public void testAliTransfer() {
      AliTransferParam transferParam =new AliTransferParam();
      //TODO 参数填充
      transferParam.setTitle("消费返利提现");
      transferParam.setAccount("13820790295");
      transferParam.setName("橘子橘子");
      transferParam.setOutNo("WD24072900000007");
      transferParam.setAmount(100000L);
      transferParam.setRemark("WD24072900000007:消费返利提现");
      paymentRpcService.transfer(transferParam);
      System.out.println("paymentRpcService = " + paymentRpcService);
    }
}
