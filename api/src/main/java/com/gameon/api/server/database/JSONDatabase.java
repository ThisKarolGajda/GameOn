package com.gameon.api.server.database;

import com.gameon.api.server.GameOnInstance;
import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
    private final File dataFolder;

    public JSONDatabase(String fileName, Class<T[]> clazz, boolean useMultiFiles) {
        this.fileName = fileName;
        this.dataFolder = GameOnInstance.getDataFolder();
        this.clazz = clazz;
        this.useMultiFiles = useMultiFiles;
        String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) ->
                        ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString()).toLocalDateTime())
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (localDate, type, jsonSerializationContext) ->
                        new JsonPrimitive(formatter.format(localDate)))
                .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
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
        File directory = new File(dataFolder, fileName + "s");
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
        File file = new File(dataFolder, fileName + ".json");
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
                e.printStackTrace();
            }
        } else {
            try {
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
        File directory = new File(dataFolder, fileName + "s");
        File entityFile = new File(directory, id + ".json");
        try (Writer writer = new FileWriter(entityFile)) {
            gson.toJson(entity, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAllToSingleFile() {
        File file = new File(dataFolder, fileName + ".json");
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(cache.values(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFromFile(PK id) {
        if (useMultiFiles) {
            File directory = new File(dataFolder, fileName + "s");
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
