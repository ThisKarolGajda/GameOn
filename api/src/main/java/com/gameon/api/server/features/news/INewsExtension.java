package com.gameon.api.server.features.news;

import com.gameon.api.server.extension.IExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface INewsExtension extends IExtension {

    Optional<NewsEntity> getLatestNews();

    List<NewsEntity> getAllNews();

    UUID createNews(NewsEntity news);

    boolean deleteNews(UUID id);

    Optional<NewsEntity> getNewsById(UUID id);
}
