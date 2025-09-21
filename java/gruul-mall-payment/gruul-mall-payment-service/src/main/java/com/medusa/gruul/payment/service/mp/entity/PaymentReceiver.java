package com.medusa.gruul.payment.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.global.model.enums.ReceiverType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.EnumTypeHandler;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/6/7
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@TableName(value = "t_payment_receiver")
public class PaymentReceiver implements Serializable {

	/**
	 * 分账接收方绑定的商户号
	 */
	private String subMchid;

	/**
	 * 分账接收方类型
	 */
	@TableField(typeHandler = EnumTypeHandler.class)
	private ReceiverType type;

	/**
	 * 分账接收方账号
	 */
	private String account;


}
