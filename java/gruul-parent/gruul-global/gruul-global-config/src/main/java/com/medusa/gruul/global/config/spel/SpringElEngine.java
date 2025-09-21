package com.medusa.gruul.global.config.spel;

import cn.hutool.extra.expression.ExpressionEngine;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * sp el 表达式解析引擎
 *
 * @author 张治保
 * @since 2023/11/10
 */
@Component
public class SpringElEngine implements ExpressionEngine {

    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private static BeanResolver beanResolver;

    public static void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringElEngine.beanResolver = new BeanFactoryResolver(applicationContext);
    }

    @Override
    public Object eval(String expression, Map<String, Object> context, Collection<Class<?>> allowClassSet) {
        return EXPRESSION_PARSER.parseExpression(expression).getValue(evaluationContext(context));
    }

    private EvaluationContext evaluationContext(Map<String, Object> context) {
        SpElContext spElContext = SpElContext.of(context);
        StandardEvaluationContext evaluationContext;
        Method method = spElContext.getMethod();
        if (method != null) {
            evaluationContext = new MethodBasedEvaluationContext(
                    null, method, spElContext.getMethodArguments(), PARAMETER_NAME_DISCOVERER
            );
        } else {
            evaluationContext = new StandardEvaluationContext();
        }
        evaluationContext.setRootObject(spElContext.getRoot());
        if (beanResolver != null) {
            evaluationContext.setBeanResolver(beanResolver);
        }
        context.forEach(evaluationContext::setVariable);
        return evaluationContext;
    }
}
