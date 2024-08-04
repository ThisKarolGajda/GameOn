package com.gameon.api.server.features.news;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.AbstractModuleInfo;
import com.gameon.api.server.extension.IExtension;
import com.gameon.api.server.extension.handler.HandlerAccessType;
import com.gameon.api.server.extension.handler.HandlerData;
import com.gameon.api.server.features.GameOnFeatureType;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

import java.util.*;

public class NewsInfo extends AbstractModuleInfo {
    private INewsExtension extension;

    @Override
    public GameOnFeatureType getType() {
        return GameOnFeatureType.NEWS;
    }

    @Override
    public Set<HandlerData> getRoutes(IExtension extension) {
        this.extension = (INewsExtension) extension;
        Set<HandlerData> routes = new HashSet<>();

        routes.add(new HandlerData(
                "latest",
                HandlerType.GET,
                HandlerAccessType.AUTHORIZED,
                this::getLatestNews
        ));

        routes.add(new HandlerData(
                "all",
                HandlerType.GET,
                HandlerAccessType.AUTHORIZED,
                this::getAllNews
        ));

        routes.add(new HandlerData(
                "create",
                HandlerType.POST,
                HandlerAccessType.ADMIN,
                this::createPost
        ));

        routes.add(new HandlerData(
                "delete/{id}",
                HandlerType.DELETE,
                HandlerAccessType.ADMIN,
                this::deletePost
        ));

        return routes;
    }

    private void deletePost(Context ctx) {
        String id = ctx.pathParam("id");
        if (id.isBlank()) {
            error(ctx, "Invalid id");
            return;
        }

        boolean success = extension.deleteNews(UUID.fromString(id));
        if (success) {
            success(ctx, "Successfully deleted news: " + id);
        } else {
            error(ctx, "Failed to delete news: " + id);
        }
    }

    private void createPost(Context ctx, UserId userId) {
        Map<String, Object> json = deserialize(ctx);
        String title = (String) json.get("title");
        String description = (String) json.get("description");
        NewsEntity entity = new NewsEntity(title, description, userId);
        UUID id = extension.createNews(entity);
        if (id == null) {
            error(ctx, "Failed to create new news");
        } else {
            success(ctx, Map.of("id", id.toString()));
        }
    }

    private void getAllNews(Context ctx) {
        List<NewsEntity> newsList = extension.getAllNews();
        success(ctx, Map.of("news", newsList));
    }

    private void getLatestNews(Context ctx) {
        Optional<NewsEntity> entity = extension.getLatestNews();
        if (entity.isEmpty()) {
            error(ctx, "No newest news!");
        } else {
            success(ctx, Map.of("news", entity.get()));
        }
    }

    @Override
    public String getDefaultPath() {
        return "news";
    }
}
