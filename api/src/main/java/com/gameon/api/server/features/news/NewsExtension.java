package com.gameon.api.server.features.news;

import com.gameon.api.server.database.JSONDatabase;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NewsExtension extends JSONDatabase<UUID, NewsEntity> implements INewsExtension {

    public NewsExtension() {
        super("news", NewsEntity[].class, false);
    }

    @Override
    public Optional<NewsEntity> getLatestNews() {
        List<NewsEntity> newsList = getAll();
        return newsList.stream()
                .max(Comparator.comparing(NewsEntity::getCreationDate));
    }

    @Override
    public List<NewsEntity> getAllNews() {
        return getAll();
    }

    @Override
    public UUID createNews(NewsEntity news) {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (isIdTaken(id));
        news.setId(id);
        save(news);
        return id;
    }

    private boolean isIdTaken(UUID id) {
        return getNewsById(id).isPresent();
    }

    @Override
    public boolean deleteNews(UUID id) {
        Optional<NewsEntity> news = getNewsById(id);
        if (news.isPresent()) {
            delete(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<NewsEntity> getNewsById(UUID id) {
        return getById(id);
    }

    @Override
    public boolean canBeUsed() {
        return true;
    }
}
