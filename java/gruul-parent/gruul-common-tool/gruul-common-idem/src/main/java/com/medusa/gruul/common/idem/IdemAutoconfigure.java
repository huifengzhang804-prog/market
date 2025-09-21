package com.medusa.gruul.common.idem;

import com.medusa.gruul.common.idem.aspect.IdemAdvisor;
import com.medusa.gruul.common.idem.aspect.IdemInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * 幂等性校验工具自动装配
 * 增加幂等响应头 IDEM
 *
 * @author 张治保
 * date 2022/2/9
 */
@Import({IdemAdvisor.class, IdemInterceptor.class})
@ConditionalOnClass(name = {"org.springframework.data.redis.core.RedisTemplate", "org.springframework.aop.support.annotation.AnnotationMatchingPointcut"})
public class IdemAutoconfigure {


}
