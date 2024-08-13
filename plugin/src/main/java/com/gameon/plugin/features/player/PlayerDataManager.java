package com.gameon.plugin.features.player;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.database.JSONDatabase;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PlayerDataManager {
    private final File playerDataDir;
    private final Gson gson = JSONDatabase.gson;

    public PlayerDataManager(File playerDataDir) {
        this.playerDataDir = playerDataDir;
    }

    public void savePlayerData(@NotNull PlayerImpl playerImpl) {
        Path playerDataFile = Paths.get(playerDataDir + playerImpl.userId().uuid().toString() + ".json.gz");
        try {
            Files.createDirectories(playerDataFile.getParent());

            try (FileOutputStream fos = new FileOutputStream(playerDataFile.toFile());
                 GZIPOutputStream gzos = new GZIPOutputStream(fos);
                 Writer writer = new OutputStreamWriter(gzos)) {
                gson.toJson(playerImpl, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlayerImpl loadPlayerData(@NotNull UserId userId) {
        Path playerDataFile = Paths.get(playerDataDir + userId.uuid().toString() + ".json.gz");

        if (Files.exists(playerDataFile)) {
            try (FileInputStream fis = new FileInputStream(playerDataFile.toFile());
                 GZIPInputStream gzis = new GZIPInputStream(fis);
                 Reader reader = new InputStreamReader(gzis)) {
                return gson.fromJson(reader, PlayerImpl.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return PlayerBuilder.of(userId).build();
    }

}
