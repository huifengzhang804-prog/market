package com.medusa.gruul.service.uaa.service.service;



import com.medusa.gruul.service.uaa.service.model.dto.account.Account;
import com.medusa.gruul.service.uaa.service.model.dto.account.AccountDTO;

import java.util.List;

/**
 * @author 张治保
 * date 2022/5/31
 */
public interface AccountService {

    /**
     * 新增用户地址
     *
     * @param account      用户
     */
    void insertNewAccount(Account account);
    int countAccountByMobile(Account account);
    int countAccountByUserName(Account account);
    Account getAccountByAccAndPwd(AccountDTO account);
    List<Account> getUserListByPhone(AccountDTO account);
    Account getUserByUserId(AccountDTO account);
    void resetPassword(AccountDTO account);
    String getToken(AccountDTO account,Boolean isUPLogin);



}
