package com.gameon.plugin.features.news;

import com.gameon.api.server.features.news.INewsExtension;
import com.gameon.api.server.features.news.NewsEntity;
import com.gameon.plugin.database.JSONDatabase;
import org.bukkit.plugin.Plugin;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NewsExtensionApi implements INewsExtension {
    private final JSONDatabase<UUID, NewsEntity> newsDatabase;

    public NewsExtensionApi(Plugin plugin) {
        this.newsDatabase = new JSONDatabase<>(plugin, "news", NewsEntity[].class, false);
    }

    @Override
    public Optional<NewsEntity> getLatestNews() {
        List<NewsEntity> newsList = newsDatabase.getAll();
        return newsList.stream()
                .max(Comparator.comparing(NewsEntity::getCreationDate));
    }

    @Override
    public List<NewsEntity> getAllNews() {
        return newsDatabase.getAll();
    }

    @Override
    public UUID createNews(NewsEntity news) {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (isIdTaken(id));
        news.setId(id);
        newsDatabase.save(news);
        return id;
    }

    private boolean isIdTaken(UUID id) {
        return getNewsById(id).isPresent();
    }

    @Override
    public boolean deleteNews(UUID id) {
        Optional<NewsEntity> news = getNewsById(id);
        if (news.isPresent()) {
            newsDatabase.delete(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<NewsEntity> getNewsById(UUID id) {
        return newsDatabase.getById(id);
    }

    @Override
    public boolean canBeUsed() {
        return true;
    }
}
