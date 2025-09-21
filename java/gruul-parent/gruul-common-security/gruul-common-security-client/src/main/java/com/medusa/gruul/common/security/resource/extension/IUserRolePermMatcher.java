package com.medusa.gruul.common.security.resource.extension;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.bean.UserMatch;

/**
 * 用户角色匹配器
 *
 * @author 张治保
 * date 2023/4/11
 */
public interface IUserRolePermMatcher extends IRolePermMatcher<IUserRolePermMatcher> {
    /**
     * 获取用户 信息
     *
     * @return SecureUser 用户信息
     */
    <T> SecureUser<T> getUser();

    /**
     * 计算结果 执行匹配
     *
     * @return 是否匹配成功
     */
    boolean match();


    /**
     * 获取用户与执行结果
     *
     * @return UserMatch 用户与执行结果信息
     */
    UserMatch userMatch();

}
