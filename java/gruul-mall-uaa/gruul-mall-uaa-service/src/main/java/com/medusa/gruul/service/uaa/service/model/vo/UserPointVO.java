package com.medusa.gruul.service.uaa.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 张治保
 * date 2022/5/24
 */
@Getter
@Setter
@ToString
public class UserPointVO {

	private String pointsName;
	private long userId;
	private long pointsId;
	private long pointsNum;

	


}
