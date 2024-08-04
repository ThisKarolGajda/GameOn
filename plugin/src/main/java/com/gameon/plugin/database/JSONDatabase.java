package com.gameon.plugin.database;

import com.google.gson.*;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SuppressWarnings("all")
public class JSONDatabase<PK extends Serializable, T> {
    private final Map<PK, T> cache = new HashMap<>();
    private final String fileName;
    private final Class<T[]> clazz;
    private final boolean useMultiFiles;
    private final Gson gson;
    private final Plugin plugin;

    public JSONDatabase(Plugin plugin, String fileName, Class<T[]> clazz, boolean useMultiFiles) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.clazz = clazz;
        this.useMultiFiles = useMultiFiles;
        String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) ->
                        ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString()).toLocalDateTime())
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (localDate, type, jsonSerializationContext) ->
                        new JsonPrimitive(formatter.format(localDate)))
                .setPrettyPrinting()
                .create();
        initialize();
    }

    public void save(T entity) {
        PK id = getIdFromEntity(entity);
        cache.put(id, entity);
        saveToFile(entity, id);
    }

    public Optional<T> getById(PK id) {
        return Optional.ofNullable(cache.get(id));
    }

    public void delete(PK id) {
        cache.remove(id);
        deleteFromFile(id);
    }

    public List<T> getAll() {
        return new ArrayList<>(cache.values());
    }

    private void initialize() {
        if (useMultiFiles) {
            loadFromMultipleFiles();
        } else {
            loadFromSingleFile();
        }
    }

    private void loadFromMultipleFiles() {
        File directory = new File(plugin.getDataFolder(), fileName + "s");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    loadEntityFromFile(file);
                }
            }
        }
    }

    private void loadFromSingleFile() {
        File file = new File(plugin.getDataFolder(), fileName + ".json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                T[] entities = gson.fromJson(reader, (Class<T[]>) clazz);
                if (entities != null) {
                    for (T entity : entities) {
                        PK id = getIdFromEntity(entity);
                        cache.put(id, entity);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                plugin.getLogger().severe("Failed to parse JSON: " + e.getMessage());
            }
        } else {
            try {
                File dataFolder = plugin.getDataFolder();
                if (!dataFolder.exists()) {
                    dataFolder.mkdirs();
                }

                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadEntityFromFile(File file) {
        try (Reader reader = new FileReader(file)) {
            T entity = gson.fromJson(reader, (Class<T>) clazz);
            if (entity != null) {
                PK id = getIdFromEntity(entity);
                cache.put(id, entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToFile(T entity, PK id) {
        if (useMultiFiles) {
            saveEntityToFile(entity, id);
        } else {
            saveAllToSingleFile();
        }
    }

    private void saveEntityToFile(T entity, PK id) {
        File directory = new File(plugin.getDataFolder(), fileName + "s");
        File entityFile = new File(directory, id + ".json");
        try (Writer writer = new FileWriter(entityFile)) {
            gson.toJson(entity, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAllToSingleFile() {
        File file = new File(plugin.getDataFolder(), fileName + ".json");
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(cache.values(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFromFile(PK id) {
        if (useMultiFiles) {
            File directory = new File(plugin.getDataFolder(), fileName + "s");
            File entityFile = new File(directory, id + ".json");
            if (entityFile.exists()) {
                entityFile.delete();
            }
        } else {
            saveAllToSingleFile();
        }
    }

    private PK getIdFromEntity(T entity) {
        try {
            Method getIdMethod = entity.getClass().getMethod("getId");
            return (PK) getIdMethod.invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get ID from entity: " + entity, e);
        }
    }
}
