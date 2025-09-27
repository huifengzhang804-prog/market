package com.medusa.gruul.service.uaa.service.service.impl;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.model.enums.UserStatus;
import com.medusa.gruul.common.security.resource.exception.SecurityException;
import com.medusa.gruul.service.uaa.api.dto.UserBaseDataDTO;
import com.medusa.gruul.service.uaa.api.enums.Gender;
import com.medusa.gruul.service.uaa.api.enums.UaaRabbit;
import com.medusa.gruul.service.uaa.service.model.dto.account.Account;
import com.medusa.gruul.service.uaa.service.model.dto.account.AccountDTO;
import com.medusa.gruul.service.uaa.service.mp.entity.ShopUserData;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.entity.UserData;
import com.medusa.gruul.service.uaa.service.mp.mapper.AccountMapper;
import com.medusa.gruul.service.uaa.service.mp.service.IShopUserDataService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserDataService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserService;
import com.medusa.gruul.service.uaa.service.service.AccountService;
import com.medusa.gruul.service.uaa.service.service.UserDataHandlerService;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户收货地址服务
 *
 * @author 张治保
 * date 2022/5/31
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final IUserService userService;
    private final UserDataHandlerService userDataHandlerService;
    private final IUserDataService userDataService;
    private RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final UserRpcService userRpcService;
    @Value("${token.uri}")
    private String uri;

    /**
     * 获取用户资料或生成默认数据
     *
     * @param userId 用户id
     * @return 用户资料
     */
    private UserData getUserDataOrGenerateDefault(Long userId) {
        return Option.of(
                this.userDataService
                        .lambdaQuery()
                        .eq(UserData::getUserId, userId)
                        .one()
        ).getOrElse(() -> userDataHandlerService.generateUserDefaultData(userId));
    }


    @Override
    public void insertNewAccount(Account account) {

        User user = new User()
                    .setUsername(account.getUsername())
                //默认密码
                .setPassword(passwordEncoder.encode(account.getPassword()))
                .setMobile(account.getPhone())
                .setStatus(UserStatus.NORMAL);
        boolean success = userService.save(user);

        Long userId = user.getId();

       UserData data = getUserDataOrGenerateDefault(userId)
                .setAvatar(StrUtil.cleanBlank(account.getHeadImgUrl()))
                .setUserName(account.getUsername())
                .setNickname(account.getUsername())
                .setGender(Gender.UNKNOWN)
                .setNumUserId(account.getNumUserId())
                .setPaymentPsw(account.getPaymentPsw())
                .setReferralCode(account.getReferralCode());
        this.userDataService.saveOrUpdate(data);

        updateUserData(account.getPhone(), data);
//    userDataHandlerService.generateUserDataAndSave(userId, account.getPhone());
//        mapper.insertNewAccount(account);
    }

    @Override
    public int countAccountByMobile(Account account) {
        return mapper.countAccountByMobile(account.getPhone());
    }

    @Override
    public int countAccountByUserName(Account account) {
        return mapper.countAccountByUserName(account.getUsername());
    }

    @Override
    public Account getAccountByAccAndPwd(AccountDTO account) {
        Account result= mapper.getAccountByAccAndPwd(account);

        if (result == null || !passwordEncoder.matches(account.getPassword(), result.getPassword())) {
            throw SecurityException.of(SecureCodes.USERNAME_PASSWORD_INVALID);
        }
        return result;
    }

    @Override
    public List<Account> getUserListByPhone(AccountDTO account) {
        return mapper.getUserListByPhone(account);
    }

    @Override
    public Account getUserByUserId(AccountDTO account) {
        return mapper.getUserByUserId(account);
    }

    @Override
    public void resetPassword(AccountDTO account) {

        account.setNewPsw(passwordEncoder.encode(account.getNewPsw()));
        mapper.resetPassword(account);
    }

    @Override
    public String getToken(AccountDTO account,Boolean isUPLogin) {

        // 1. 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("client-type", "CONSUMER"); // 或者 "app", "mini-program"等
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 2. 创建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", account.getUsername());
        requestBody.put("password", account.getPassword());
        // 3. 创建HttpEntity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        // 4. 发送POST请求
        restTemplate=new RestTemplate();
        String url;
        if (isUPLogin) {
            url = uri+"uplogin";
        }else{
            url = uri+"upLoginWithId";
        }

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        JSONObject jsonObject = JSONObject.parseObject(response.getBody());
        String token =jsonObject.getJSONObject("data").getString("value");
        return token;


    }



    private void updateUserData(String mobile, UserData userData) {
        Long userId = userData.getUserId();
        String avatar = userData.getAvatar();
        String nickname = userData.getNickname();
        this.rabbitTemplate.convertAndSend(
                UaaRabbit.UPDATE_DATA.exchange(), UaaRabbit.UPDATE_DATA.routingKey(),
                new UserBaseDataDTO()
                        .setUserId(userId)
                        .setNickname(nickname)
                        .setAvatar(avatar)
        );
        userRpcService.updateUser(
                new com.medusa.gruul.user.api.model.dto.UserDataDTO()
                        .setUserId(userId)
                        .setUsername(userData.getUserName())
                        .setNickname(nickname)
                        .setAvatar(avatar)
                        .setMobile(mobile)
                        .setGender(userData.getGender().name())
                        .setBirthday(userData.getBirthday())
        );
    }
}
