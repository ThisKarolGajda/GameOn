package com.gameon.api.server.features.economy;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.IModuleInfo;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import com.gameon.api.server.extension.handler.HandlerData;
import com.gameon.api.server.features.GameOnFeatureType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EconomyInfo implements IModuleInfo {
    private IEconomy economy;
    private Gson gson;

    @Override
    public GameOnFeatureType getType() {
        return GameOnFeatureType.ECONOMY;
    }

    @Override
    public Set<HandlerData> getRoutes(IExtension extension) {
        this.gson = new Gson();
        this.economy = (IEconomy) extension;
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
        Map<String, Object> response = new HashMap<>();
        response.put("balance", balance);
        ctx.status(HttpStatus.OK).json(response);
    }

    private void deposit(Context ctx) {
        String uuid = ctx.pathParam("uuid");
        double amount = ctx.bodyAsClass(Double.class);
        UserId userId = getUserIdFromUuid(uuid);
        economy.deposit(userId, amount);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Deposited " + amount + " to " + uuid);
        ctx.status(HttpStatus.OK).json(response);
    }

    private void withdraw(Context ctx) {
        String uuid = ctx.pathParam("uuid");
        double amount = ctx.bodyAsClass(Double.class);
        UserId userId = getUserIdFromUuid(uuid);
        if (economy.hasSufficientBalance(userId, amount)) {
            economy.withdraw(userId, amount);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Withdrew " + amount + " from " + uuid);
            ctx.status(HttpStatus.OK).json(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Insufficient balance");
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        }
    }

    @Nullable
    private UserId getTransferFromUserId(Context ctx) {
        Map<String, String> transferData;

        try {
            transferData = gson.fromJson(ctx.body(), new TypeToken<Map<String, String>>() {}.getType());
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", "Invalid request body"));
            return null;
        }

        String fromUuid = transferData.get("from");
        return getUserIdFromUuid(fromUuid);
    }

    private void transfer(Context ctx, UserId owner) {
        Map<String, String> transferData;

        try {
            transferData = gson.fromJson(ctx.body(), new TypeToken<Map<String, String>>() {}.getType());
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", "Invalid request body"));
            return;
        }

        String toUuid = transferData.get("to");
        double amount = Double.parseDouble(transferData.get("amount"));
        UserId toUserId = getUserIdFromUuid(toUuid);

        if (economy.hasSufficientBalance(owner, amount)) {
            economy.transfer(owner, toUserId, amount);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Transferred " + amount + " from " + owner + " to " + toUuid);
            ctx.status(HttpStatus.OK).json(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Insufficient balance for transfer");
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        }
    }

    private void setBalance(Context ctx) {
        String uuid = ctx.pathParam("uuid");
        double newBalance = ctx.bodyAsClass(Double.class);
        UserId userId = getUserIdFromUuid(uuid);
        economy.setBalance(userId, newBalance);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Set balance of " + uuid + " to " + newBalance);
        ctx.status(HttpStatus.OK).json(response);
    }

    private void getAllBalances(Context ctx) {
        Map<UserId, Double> allBalances = economy.getAllUserBalances();
        ctx.status(HttpStatus.OK).json(allBalances);
    }

    private UserId getUserIdFromUuid(String uuid) {
        return UserId.fromUuid(uuid);
    }

    @Override
    public String getDefaultPath() {
        return "economy";
    }
}
