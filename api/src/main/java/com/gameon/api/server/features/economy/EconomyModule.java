package com.gameon.api.server.features.economy;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.AbstractModule;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import com.gameon.api.server.extension.handler.HandlerData;
import com.google.gson.reflect.TypeToken;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EconomyModule extends AbstractModule {
    private IEconomyExtension economy;

    @Override
    public Set<HandlerData> getRoutes(IExtension extension) {
        this.economy = (IEconomyExtension) extension;
        Set<HandlerData> routes = new HashSet<>();

        routes.add(new HandlerData("balance/{uuid}", HandlerType.GET, this::getBalance));
        routes.add(new HandlerData("deposit/{uuid}", HandlerType.POST, HandlerAccessType.ADMIN, this::deposit));
        routes.add(new HandlerData("withdraw/{uuid}", HandlerType.POST, HandlerAccessType.ADMIN, this::withdraw));
        routes.add(new HandlerData("transfer", HandlerType.POST, HandlerAccessType.OWNER, this::getTransferFromUserId, this::transfer));
        routes.add(new HandlerData("set/{uuid}", HandlerType.POST, HandlerAccessType.ADMIN, this::setBalance));
        routes.add(new HandlerData("all", HandlerType.GET, this::getAllBalances));

        return routes;
    }

    private void getBalance(Context ctx) {
        String uuid = ctx.pathParam("uuid");
        UserId userId = getUserIdFromUuid(uuid);
        double balance = economy.getBalance(userId);
        success(ctx, Map.of("balance", balance));
    }

    private void deposit(Context ctx) {
        String uuid = ctx.pathParam("uuid");
        double amount = ctx.bodyAsClass(Double.class);
        UserId userId = getUserIdFromUuid(uuid);
        economy.deposit(userId, amount);
        success(ctx, "Deposited " + amount + " to " + uuid);
    }

    private void withdraw(Context ctx) {
        String uuid = ctx.pathParam("uuid");
        double amount = ctx.bodyAsClass(Double.class);
        UserId userId = getUserIdFromUuid(uuid);
        if (economy.hasSufficientBalance(userId, amount)) {
            economy.withdraw(userId, amount);
            success(ctx, "Withdrew " + amount + " from " + uuid);
        } else {
            error(ctx, "Insufficient balance");
        }
    }

    @Nullable
    private UserId getTransferFromUserId(Context ctx) {
        Map<String, String> transferData = deserialize(ctx);
        String fromUuid = transferData.get("from");
        return getUserIdFromUuid(fromUuid);
    }

    private void transfer(Context ctx, UserId owner) {
        Map<String, String> transferData;

        try {
            transferData = getGson().fromJson(ctx.body(), new TypeToken<Map<String, String>>() {}.getType());
        } catch (Exception e) {
            error(ctx, "Invalid request body");
            return;
        }

        String toUuid = transferData.get("to");
        double amount = Double.parseDouble(transferData.get("amount"));
        UserId toUserId = getUserIdFromUuid(toUuid);

        if (economy.hasSufficientBalance(owner, amount)) {
            economy.transfer(owner, toUserId, amount);
            success(ctx, "Transferred " + amount + " from " + owner + " to " + toUuid);
        } else {
            error(ctx, "Insufficient balance");
        }
    }

    private void setBalance(Context ctx) {
        String uuid = ctx.pathParam("uuid");
        double newBalance = ctx.bodyAsClass(Double.class);
        UserId userId = getUserIdFromUuid(uuid);
        economy.setBalance(userId, newBalance);
        success(ctx, "Set balance of " + uuid + " to " + newBalance);
    }

    private void getAllBalances(Context ctx) {
        Map<UserId, Double> allBalances = economy.getAllUserBalances();
        ctx.status(HttpStatus.OK).json(allBalances);
    }

    private UserId getUserIdFromUuid(String uuid) {
        return UserId.fromUuidString(uuid);
    }

    @Override
    public String getDefaultPath() {
        return "economy";
    }
}
