package com.medusa.gruul.service.uaa.service.service;

import com.medusa.gruul.service.uaa.api.dto.UserAuthorityDTO;
import com.medusa.gruul.service.uaa.service.model.dto.UserDataDTO;
import com.medusa.gruul.service.uaa.service.model.vo.UserAccountVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserDataVO;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.entity.UserData;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 张治保
 * date 2022/5/24
 */
public interface UserDataHandlerService {
    /**
     * 查询个人资料
     *
     * @param userId 用户id
     * @return 查询结果
     */
    UserDataVO getUserDataById(Long userId);

    /**
     * 修改个人资料
     *
     * @param userData 个人资料数据
     */
    void saveUserData(UserDataDTO userData);

    /**
     * 生成用户信息
     *
     * @param mobile 用户手机号
     * @param openid openid
     * @return 用户信息
     */
    User generateUser(String mobile, String openid);

    /**
     * 生成默认消费者资料
     *
     * @param userId 用户id
     * @return 消费者资料
     */
    UserData generateUserDefaultData(Long userId);

    /**
     * 生成消费者默认资料 并保存
     *
     * @param userId 用户id
     * @param mobile 手机号
     */
    void generateUserDataAndSave(Long userId, String mobile);

    /**
     * 查询我的账号资料
     *
     * @return 用户账号资料
     */
    UserAccountVO getMyAccount();

    /**
     * 设置用户权限
     *
     * @param authority 用户权限设置dto
     */
    void setUserAuthority(UserAuthorityDTO authority);

    /**
     * 批量导入用户
     *
     * @param file 文件
     */
    void importUsers(MultipartFile file);

}
