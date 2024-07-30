package com.gameon.api.server.features.economy;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.IExtension;

import java.util.Map;

public interface IEconomy extends IExtension {

    double getBalance(UserId userId);

    boolean hasSufficientBalance(UserId userId, double amount);

    void withdraw(UserId userId, double amount);

    void deposit(UserId userId, double amount);

    void transfer(UserId fromUserId, UserId toUserId, double amount);

    void setBalance(UserId userId, double amount);

    Map<UserId, Double> getAllUserBalances();
}
