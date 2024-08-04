package com.gameon.api.server.features.economy;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.IExtension;

import java.util.Map;

public interface IEconomyExtension extends IExtension {
    /**
     * Retrieves the balance of the specified user.
     *
     * @param userId The UserId of the user whose balance is to be retrieved.
     * @return The balance of the user as a double.
     */
    double getBalance(UserId userId);

    /**
     * Checks if the specified user has a sufficient balance for a given amount.
     *
     * @param userId The UserId of the user whose balance is to be checked.
     * @param amount The amount to check against the user's balance.
     * @return True if the user has sufficient balance, false otherwise.
     */
    boolean hasSufficientBalance(UserId userId, double amount);

    /**
     * Withdraws a specified amount from the user's balance.
     *
     * @param userId The UserId of the user from whose account the amount will be withdrawn.
     * @param amount The amount to withdraw.
     */
    void withdraw(UserId userId, double amount);

    /**
     * Deposits a specified amount into the user's balance.
     *
     * @param userId The UserId of the user to whom the amount will be deposited.
     * @param amount The amount to deposit.
     */
    void deposit(UserId userId, double amount);

    /**
     * Transfers a specified amount from one user to another.
     *
     * @param fromUserId The UserId of the user from whom the amount will be transferred.
     * @param toUserId The UserId of the user to whom the amount will be transferred.
     * @param amount The amount to transfer.
     */
    void transfer(UserId fromUserId, UserId toUserId, double amount);

    /**
     * Sets the balance of the specified user to a given amount.
     *
     * @param userId The UserId of the user whose balance will be set.
     * @param amount The new balance to set for the user.
     */
    void setBalance(UserId userId, double amount);

    /**
     * Retrieves a map of all user balances.
     *
     * @return A map where the key is the UserId and the value is the user's balance.
     */
    Map<UserId, Double> getAllUserBalances();
}
