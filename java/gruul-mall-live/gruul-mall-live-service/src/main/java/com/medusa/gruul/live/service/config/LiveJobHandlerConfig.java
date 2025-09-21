package com.medusa.gruul.live.service.config;

import com.medusa.gruul.live.service.task.LiveProductReviewJob;
import com.medusa.gruul.live.service.task.LiveRoomReviewJob;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * xxl job config
 *
 * @author 张治保
 */
@Configuration
@RequiredArgsConstructor
public class LiveJobHandlerConfig {

    private final LiveRoomReviewJob liveRoomReviewJob;
    private final LiveProductReviewJob liveProductReviewJob;

    @XxlJob("LiveRoomReviewJob")
    public void liveCloseJobHandler() {
        liveRoomReviewJob.execute();
    }

    @XxlJob("LiveProductReviewJob")
    public void liveRoomReviewJob() {
        liveProductReviewJob.execute();
    }
}
