package com.medusa.gruul.global.config.spel;

import cn.hutool.core.lang.Dict;
import cn.hutool.extra.expression.ExpressionUtil;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * sp el 表达式上下文 用于准备执行表达式的上下文信息
 * example ExpressionUtil.eval("a+b", SpElContext.of().set("a", 1).set("b", 2)) = 3
 * example ExpressionUtil.eval("#a+#b", SpElContext.of().method(method, arguments))
 *
 * @author 张治保
 * @see ExpressionUtil,SpringElEngine
 * @since 2023/11/10
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SpElContext extends Dict {

    private static final String ROOT_OBJECT = SpringElEngine.class.getName() + "rootObject";
    private static final String METHOD = SpringElEngine.class.getName() + "method";
    private static final String METHOD_ARGUMENTS = SpringElEngine.class.getName() + "methodArguments";
    private static final String RESULT = "result";


    private SpElContext(Map<String, Object> map) {
        super(map);
    }

    /**
     * 初始化 SpElContext 对象
     *
     * @return 一个新的SpElContext
     */
    public static SpElContext of() {
        return new SpElContext();
    }

    /**
     * 初始化 SpElContext 对象
     *
     * @param context 上下文初始化
     */
    public static SpElContext of(Map<String, Object> context) {
        if (context instanceof SpElContext spElContext) {
            return spElContext;
        }
        return new SpElContext(context);
    }

    /**
     * 设置根对象 可以通过 spring el 直接调用根对象的方法
     *
     * @param root 根对象
     * @return this
     */
    public SpElContext root(Object root) {
        return (SpElContext) this.set(ROOT_OBJECT, root);


    }

    /**
     * 设置方法和方法参数
     *
     * @param method    方法
     * @param arguments 方法参数
     * @return this
     */
    public SpElContext method(Method method, Object[] arguments) {
        return (SpElContext) this.set(METHOD, method)
                .set(METHOD_ARGUMENTS, arguments);
    }

    /**
     * 设置方法返回值
     *
     * @param result 方法返回值
     * @return this
     */
    public SpElContext result(Object result) {
        return (SpElContext) this.set(RESULT, result);
    }

    /**
     * 获取方法
     *
     * @return 方法
     */
    public Method getMethod() {
        return (Method) get(METHOD);
    }

    /**
     * 获取方法参数
     *
     * @return 方法参数
     */
    public Object[] getMethodArguments() {
        return (Object[]) get(METHOD_ARGUMENTS);
    }

    /**
     * 获取根对象
     *
     * @return 根对象
     */
    public Object getRoot() {
        return get(ROOT_OBJECT);
    }

}
