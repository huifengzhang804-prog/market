package com.medusa.gruul.common.idem.aspect;

import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.global.model.constant.AspectOrder;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.lang.NonNull;

/**
 * @author 张治保
 * date 2022/2/10
 */
public class IdemAdvisor extends AbstractPointcutAdvisor {

    /**
     * 方法增强
     */
    private final Advice advice;
    /**
     * 切点
     */
    private final Pointcut pointcut;

    public IdemAdvisor(IdemInterceptor advice) {
        this.advice = advice;
        this.pointcut = AnnotationMatchingPointcut.forMethodAnnotation(Idem.class);
        super.setOrder(AspectOrder.IDEM_ASPECT);
    }

    @Override
    @NonNull
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    @NonNull
    public Advice getAdvice() {
        return advice;
    }
}
