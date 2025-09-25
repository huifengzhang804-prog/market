package com.medusa.gruul.service.uaa.service.mp.mapper;


import com.medusa.gruul.service.uaa.service.model.dto.account.Account;
import com.medusa.gruul.service.uaa.service.model.dto.account.AccountDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountMapper {
    void insertNewAccount(Account account);
    int countAccountByMobile(@Param("phone") String phone);
    int countAccountByUserName(@Param("username") String userName);
    Account getAccountByAccAndPwd(AccountDTO account);
    List<Account> getUserListByPhone(AccountDTO account);
    Account getUserByUserId(AccountDTO account);
    void resetPassword(AccountDTO account);
}
