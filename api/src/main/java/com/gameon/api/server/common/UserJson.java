package com.gameon.api.server.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class UserJson {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static String toJson(User user) {
        if (user == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing User to JSON", e);
        }
    }

    public static User fromJson(String json) {
        try {
            return objectMapper.readValue(json, User.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing User from JSON", e);
        }
    }
}
