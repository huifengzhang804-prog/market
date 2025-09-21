package com.medusa.gruul.service.uaa.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.model.enums.UserStatus;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.constant.SecurityConst;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.service.uaa.api.constant.UaaConstant;
import com.medusa.gruul.service.uaa.api.dto.UserAuthorityDTO;
import com.medusa.gruul.service.uaa.api.dto.UserBaseDataDTO;
import com.medusa.gruul.service.uaa.api.dto.UserRegisterDTO;
import com.medusa.gruul.service.uaa.api.dto.UserRegisterDataDTO;
import com.medusa.gruul.service.uaa.api.enums.Gender;
import com.medusa.gruul.service.uaa.api.enums.UaaRabbit;
import com.medusa.gruul.service.uaa.api.excel.DataListener;
import com.medusa.gruul.service.uaa.service.addon.UaaAddonSupporter;
import com.medusa.gruul.service.uaa.service.model.dto.UserDataDTO;
import com.medusa.gruul.service.uaa.service.model.enums.UaaError;
import com.medusa.gruul.service.uaa.service.model.vo.UserAccountVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserDataVO;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.entity.UserData;
import com.medusa.gruul.service.uaa.service.mp.entity.UserRole;
import com.medusa.gruul.service.uaa.service.mp.service.IUserDataService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserService;
import com.medusa.gruul.service.uaa.service.properties.UaaProperties;
import com.medusa.gruul.service.uaa.service.service.UserDataHandlerService;
import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/5/24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataHandlerServiceImpl implements UserDataHandlerService {

    private final IUserDataService userDataService;
    private final RabbitTemplate rabbitTemplate;
    private final UaaProperties uaaProperties;
    private final IUserRoleService userRoleService;
    private final IUserService userService;
    private final UserRpcService userRpcService;
    private final UaaAddonSupporter uaaAddonSupporter;
    private final PasswordEncoder passwordEncoder;
    private final Executor uaaExecutor;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;


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
        ).getOrElse(() -> this.generateUserDefaultData(userId));
    }

    @Override
    public UserDataVO getUserDataById(Long userId) {
        //查询用户分销码异步任务
        CompletableFuture<String> distributorCodeTask = CompletableFuture.supplyAsync(() -> uaaAddonSupporter.distributorCode(userId), uaaExecutor);
        //查询用户资料异步任务
        CompletableFuture<UserData> userDataTask = CompletableFuture.supplyAsync(() -> getUserDataOrGenerateDefault(userId), uaaExecutor);
        //获取分销码 异常不抛出
        String distributorCode = null;
        try {
            distributorCode = distributorCodeTask.get();
        } catch (Exception exception) {
            log.error("get distributor code error", exception);
        }
        //设置分销吗
        UserData userData = CompletableTask.getOrThrowException(userDataTask);
        return new UserDataVO()
                .setUserId(userData.getUserId())
                .setUserName(userData.getUserName())
                .setNickname(userData.getNickname())
                .setAvatar(userData.getAvatar())
                .setGender(userData.getGender())
                .setBirthday(userData.getBirthday())
                .setDistributorCode(distributorCode);
    }

    @Override
    public void saveUserData(UserDataDTO userData) {
        SecureUser<?> secureUser = ISecurity.userMust();
        //H5端，取当前登录信息,其他角色，取userData的userId
        if (!ISecurity.anyRole(Roles.USER)) {
            secureUser.setId(userData.getUserId());
        }
        Long userId = secureUser.getId();
        UserData data = getUserDataOrGenerateDefault(userId)
                .setAvatar(StrUtil.cleanBlank(userData.getAvatar()))
                .setUserName(userData.getUserName())
                .setNickname(StrUtil.cleanBlank(userData.getNickname()))
                .setBirthday(Option.of(userData.getBirthday()).getOrNull())
                .setGender(userData.getGender());
        this.userDataService.saveOrUpdate(data);
        this.updateUserData(secureUser.getMobile(), data);
        //通知刷新用户信息
        ISecurity.refreshUsers(
                ClientType.CONSUMER,
                SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID,
                Set.of(userId)
        );
        uaaExecutor.execute(() -> pigeonChatStatisticsRpcService.removeIMUserInfoCache(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = UaaConstant.USER_LOGIN_REG_LOCK, key = "#mobile")
    public User generateUser(String mobile, String openid) {
        Optional<User> userOpt = userService.lambdaQuery().eq(User::getMobile, mobile).oneOpt();
        if (userOpt.isPresent()) {
            return userOpt.get();
        }
        User user = new User()
                .setMobile(mobile)
                .setOpenid(openid)
                .setStatus(UserStatus.NORMAL)
                .setPassword(passwordEncoder.encode(RandomUtil.randomString(15)));
        userService.save(user);
        Long userId = user.getId();
        this.generateUserDataAndSave(userId, mobile);
        return user;
    }

    @Override
    public UserData generateUserDefaultData(Long userId) {
        return new UserData()
                .setUserId(userId)
                .setGender(Gender.UNKNOWN)
                .setNickname(ISecurity.generateNickName(userId).getOrNull())
                .setAvatar(this.uaaProperties.getExtension().getDefaultConsumerAvatar());
    }

    @Override
    public void generateUserDataAndSave(Long userId, String mobile) {
        UserData userData = generateUserDefaultData(userId);
        this.userDataService.save(userData);
        this.updateUserData(mobile, userData);
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

    @Override
    public UserAccountVO getMyAccount() {
        SecureUser<?> user = ISecurity.userMust();
        return new UserAccountVO()
                .setUsername(user.getUsername())
                .setMobile(user.getMobile())
                .setManageMobile(user.getMobile())
                .setEmail(user.getEmail())
                .setOpenid(user.getOpenid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setUserAuthority(UserAuthorityDTO authority) {
        //清空用户原有角色
        this.userRoleService.lambdaUpdate()
                .in(UserRole::getUserId, authority.getUserIds())
                .in(UserRole::getRoleId, Roles.FORBIDDEN_COMMENT.getRoleId(), Roles.FORBIDDEN_ORDER.getRoleId())
                .remove();
        //只允许设置 禁止评价与禁止下单角色
        List<Roles> currentRoles = authority.getRoles()
                .stream()
                .filter(roles -> roles.equals(Roles.FORBIDDEN_COMMENT) || roles.equals(Roles.FORBIDDEN_ORDER))
                .toList();
        //渲染用户角色绑定关系
        List<UserRole> userRoles = authority.getUserIds()
                .stream()
                .flatMap(
                        userId -> currentRoles.stream()
                                .map(
                                        role -> {
                                            UserRole userRole = new UserRole();
                                            userRole.setId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(userRole).longValue());
                                            userRole.setUserId(userId);
                                            userRole.setRoleId(role.getRoleId());
                                            userRole.setShopId((long) CommonPool.NUMBER_ZERO);
                                            userRole.setEnable(Boolean.TRUE);
                                            return userRole;
                                        }
                                )
                )
                .toList();
        TenantShop.disable(() -> this.userRoleService.saveBatch(userRoles));
        //设置 用户下线
        ISecurity.refreshUsers(ClientType.CONSUMER, SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID, authority.getUserIds());
        this.rabbitTemplate.convertAndSend(UaaRabbit.UPDATE_AUTHORITY.exchange(), UaaRabbit.UPDATE_AUTHORITY.routingKey(), authority);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importUsers(MultipartFile file) {
        //获取输入流
        InputStream importExcel;
        try {
            importExcel = file.getInputStream();
        } catch (IOException e) {
            throw new GlobalException("excel read error");
        }
        //解析 excel
        DataListener<UserRegisterDTO> listener = new DataListener<>();
        try {
            EasyExcel.read(importExcel, UserRegisterDTO.class, listener)
                    .sheet()
                    .headRowNumber(6)
                    .doRead();
        } catch (ExcelDataConvertException e) {
            throw UaaError.EXCEL_DATA_READ_ERROR.exception(
                    (e.getRowIndex() + 6), e.getColumnIndex(), e.getMessage(), e.getExcelContentProperty()
            );
        }
        List<UserRegisterDTO> registers = listener.getDataList();
        if (CollectionUtils.isEmpty(registers)) {
            return;
        }
        // 校验数据
        this.validData(registers);
        // 保存数据
        List<UserRegisterDataDTO> userDataList = saveUserAndData(registers);
        rabbitTemplate.convertAndSend(
                UaaRabbit.BATCH_REGISTER.exchange(),
                UaaRabbit.BATCH_REGISTER.routingKey(),
                userDataList
        );
    }

    private List<UserRegisterDataDTO> saveUserAndData(List<UserRegisterDTO> dataList) {
        Set<String> mobiles = new HashSet<>(dataList.size());
        List<User> users = dataList.stream()
                .map(userRegister -> {
                    mobiles.add(userRegister.getMobile());
                    return new User()
                            .setMobile(userRegister.getMobile())
                            .setStatus(UserStatus.NORMAL)
                            .setPassword(passwordEncoder.encode(RandomUtil.randomString(15)));
                }).toList();
        List<User> userList = userService.lambdaQuery()
                .select(User::getMobile)
                .in(User::getMobile, mobiles)
                .list();
        if (CollUtil.isNotEmpty(userList)) {
            throw UaaError.MOBILE_HAS_BEEN_USED.dataEx(
                    userList.stream().map(User::getMobile).collect(Collectors.toSet())
            );
        }
        // 批量插入用户
        boolean success = userService.saveBatch(users);
        if (!success) {
            throw SystemCode.DATA_ADD_FAILED.exception();
        }
        Map<String, UserRegisterDTO> mobileUserRegisterMap = dataList.stream()
                .collect(Collectors.toMap(UserRegisterDTO::getMobile, v -> v));
        // 批量插入用户数据
        List<UserRegisterDataDTO> result = CollUtil.newArrayList();
        String defaultConsumerAvatar = uaaProperties.getExtension().getDefaultConsumerAvatar();
        List<UserData> userData = users.stream()
                .map(user -> {
                            UserRegisterDTO userRegister = mobileUserRegisterMap.get(user.getMobile());
                            Long userId = user.getId();
                            String userName = userRegister.getUserName();
                            String nickname = ISecurity.generateNickName(userName, userId).get();
                            Gender gender = userRegister.getGender();
                            LocalDate birthday = userRegister.getBirthday();
                            result.add(
                                    new UserRegisterDataDTO()
                                            .setUserId(userId)
                                            .setAvatar(defaultConsumerAvatar)
                                            .setUserName(userName)
                                            .setNickname(nickname)
                                            .setGender(gender)
                                            .setBirthday(birthday)
                                            .setMobile(userRegister.getMobile())
                                            .setRankCode(userRegister.getMemberRankCode())
                                            .setBalance(userRegister.getBalance())
                                            .setIntegralTotal(userRegister.getIntegralTotal())
                                            .setMemberType(userRegister.getMemberType())
                            );
                            return new UserData()
                                    .setUserId(userId)
                                    .setNickname(nickname)
                                    .setGender(gender)
                                    .setBirthday(birthday)
                                    .setAvatar(defaultConsumerAvatar)
                                    .setBirthday(birthday);
                        }

                ).toList();
        success = userDataService.saveBatch(userData);
        if (!success) {
            throw SystemCode.DATA_ADD_FAILED.exception();
        }
        return result;
    }

    private void validData(List<UserRegisterDTO> dataList) {
        int freeRankCode = 0;
        int paidRankCode = 0;
        Set<Integer> paidCodes = new HashSet<>();
        for (int i = 0; i < dataList.size(); i++) {
            UserRegisterDTO userRegister = dataList.get(i);
            String rankCode = userRegister.getRankCode();
            MemberType memberType = userRegister.getMemberType();
            if ((memberType != null && rankCode == null) || (rankCode != null && memberType == null)) {
                throw UaaError.MEMBER_TYPE_AND_LEVEL_MUST_EXIST_OR_EMPTY.exception(i + 7);
            }
            if (memberType == null) {
                userRegister.setMemberType(MemberType.FREE_MEMBER);
                userRegister.setRankCode("1");
                continue;
            }
            boolean containsRankCode = rankCode.toLowerCase().contains("s");
            if (memberType == MemberType.FREE_MEMBER && containsRankCode || (memberType == MemberType.PAID_MEMBER && !containsRankCode)) {
                throw UaaError.MEMBER_TYPE_AND_LEVEL_NOT_MATCH.exception(i + 7);
            }
            Pattern compilePattern = Pattern.compile(UaaConstant.MEMBER_RANK_REGEX);
            Matcher matcher = compilePattern.matcher(rankCode);
            int matchCode = 0;
            if (matcher.find()) {
                matchCode = Integer.parseInt(matcher.group(1));
            }
            switch (userRegister.getMemberType()) {
                case FREE_MEMBER -> freeRankCode = Math.max(freeRankCode, matchCode);
                case PAID_MEMBER -> paidCodes.add(matchCode);
            }
            userRegister.setMemberRankCode(matchCode);
        }
        if (freeRankCode > 0) {
            Integer maxFreeMemberRankCode = userRpcService.getFreeMemberRankCode();
            if (maxFreeMemberRankCode == null || freeRankCode > maxFreeMemberRankCode) {
                throw UaaError.FREE_MEMBER_LEVEL_SETTING_ERROR.exception(maxFreeMemberRankCode);
            }
        }
        if (CollUtil.isNotEmpty(paidCodes)) {
            List<Integer> paidRankCodes = uaaAddonSupporter.getMaxPaidMemberRankCode(paidRankCode);
            if (CollUtil.isEmpty(paidCodes) || !new HashSet<>(paidRankCodes).containsAll(paidCodes)) {
                throw UaaError.PAID_MEMBER_LEVEL_SETTING_ERROR.dataEx(paidRankCodes);
            }
        }
    }

}
