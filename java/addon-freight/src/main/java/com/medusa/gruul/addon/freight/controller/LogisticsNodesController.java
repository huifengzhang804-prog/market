package com.medusa.gruul.addon.freight.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.kuaidi100.sdk.pojo.HttpResult;
import com.kuaidi100.sdk.response.QueryTrackMapResp;
import com.medusa.gruul.addon.freight.model.param.LogisticsNodeInfoParam;
import com.medusa.gruul.addon.freight.service.LogisticsNodesService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 物流节点控制层
 *
 * @author xiaoq
 * @Description 物流节点控制层
 * @date 2022-06-09 15:37
 */
@RestController
@RequestMapping("logistics")
@RequiredArgsConstructor
public class LogisticsNodesController {


	private final LogisticsNodesService logisticsNodesService;

	/**
	 * 物流节点获取
	 *
	 * @param logisticsNodeInfoParam 物流节点查询信息
	 * @return Map<String, Object>  map-->  com.kuaidi100.sdk.pojo.HttpResult
	 */
	@Idem(1000)
	@GetMapping("node")
	@Log("物流节点获取")
	public Result<Map<String, Object>> getLogisticsNode(LogisticsNodeInfoParam logisticsNodeInfoParam) {
		HttpResult logisticsNode = logisticsNodesService.getLogisticsNode(logisticsNodeInfoParam);
		return Result.ok(
				logisticsNode != null && StrUtil.isNotBlank(logisticsNode.getBody()) ? JSON.parseObject(logisticsNode.getBody(), new TypeReference<>() {
				}) : Collections.emptyMap()
		);
	}

	/**
	 * 物流节点地图轨迹获取 暂未调用预留接口
	 *
	 * @param logisticsNodeInfoParam 地图轨迹查询信息
	 * @return Map<String, Object>  mapTrack-->  com.kuaidi100.sdk.response.QueryTrackMapResp
	 */
	@Idem(1000)
	@GetMapping("map/track")
	public Result<Map<String, Object>> getLogisticsMapTrack(LogisticsNodeInfoParam logisticsNodeInfoParam) {
		QueryTrackMapResp logisticsMapTrack = logisticsNodesService.getLogisticsMapTrack(logisticsNodeInfoParam);
		Map<String, Object> mapTrack = new HashMap<>(logisticsMapTrack.getData().size());
		mapTrack.put("data", logisticsMapTrack.getData());
		mapTrack.put("trailUrl", logisticsMapTrack.getTrailUrl());
		return Result.ok(mapTrack);
	}
}
