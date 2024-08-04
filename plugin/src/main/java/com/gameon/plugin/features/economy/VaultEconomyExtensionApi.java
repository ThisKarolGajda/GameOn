package com.gameon.plugin.features.economy;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.economy.IEconomyExtension;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class VaultEconomyExtensionApi implements IEconomyExtension {
    private final Economy economy;

    public VaultEconomyExtensionApi() {
        this.economy = setupEconomy();
    }

    @Override
    public boolean canBeUsed() {
        return economy != null;
    }

    @Nullable
    private Economy setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return null;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return null;
        }

        return rsp.getProvider();
    }

    @Override
    public double getBalance(@NotNull UserId userId) {
        return economy.getBalance(Bukkit.getOfflinePlayer(userId.uuid()));
    }

    @Override
    public boolean hasSufficientBalance(UserId user, double amount) {
        return getBalance(user) >= amount;
    }

    @Override
    public void withdraw(UserId user, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot withdraw a negative amount.");
        }

        EconomyResponse response = economy.withdrawPlayer(Bukkit.getOfflinePlayer(user.uuid()), amount);
        if (!response.transactionSuccess()) {
            throw new RuntimeException("Failed to withdraw: " + response.errorMessage);
        }
    }

    @Override
    public void deposit(UserId user, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot deposit a negative amount.");
        }

        EconomyResponse response = economy.depositPlayer(Bukkit.getOfflinePlayer(user.uuid()), amount);
        if (!response.transactionSuccess()) {
            throw new RuntimeException("Failed to deposit: " + response.errorMessage);
        }
    }

    @Override
    public void transfer(UserId fromUser, UserId toUser, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot transfer a negative amount.");
        }

        withdraw(fromUser, amount);
        deposit(toUser, amount);
    }

    @Override
    public void setBalance(UserId user, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot set a negative balance.");
        }

        double currentBalance = getBalance(user);
        if (amount > currentBalance) {
            deposit(user, amount - currentBalance);
        } else if (amount < currentBalance) {
            withdraw(user, currentBalance - amount);
        }
    }

    @Override
    public Map<UserId, Double> getAllUserBalances() {

        //TODO: This method requires additional implementation as Vault does not provide a direct way to get all balances.
        return new HashMap<>();
    }
}
