package com.medusa.gruul.user.service.task;


import com.medusa.gruul.user.service.mp.service.IUserFootMarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

/**
 * @author: WuDi
 * @date: 2022/9/28
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class UserFootMarkTask {


	private final IUserFootMarkService userFootMarkService;

	@Scheduled(cron = "0 59 23 L * ?")
	public void configureTasks() {
		userFootMarkService.lambdaUpdate()
				.apply("DATE_SUB(CURDATE(), INTERVAL 1 MONTH) >= date(update_time)")
				.remove();
		log.warn("执行静态定时任务时间: " + LocalDateTime.now());
	}

}
