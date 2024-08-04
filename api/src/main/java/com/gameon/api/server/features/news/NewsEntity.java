package com.gameon.api.server.features.news;

import com.gameon.api.server.common.UserId;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class NewsEntity {
    private UUID id;
    private LocalDateTime creationDate;
    private UUID authorUuid;
    private String title;
    private String description;
    private String image;

    public NewsEntity(UUID id, String title, String description, String image, UUID authorUuid) {
        this.id = id;
        this.creationDate = LocalDateTime.now();
        this.authorUuid = authorUuid;
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public NewsEntity() {
    }

    public NewsEntity(String title, String description, UserId author) {
        this(null, title, description, null, author.uuid());
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (NewsEntity) obj;
        return Objects.equals(this.title, that.title) &&
                Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description);
    }

    @Override
    public String toString() {
        return "NewsEntity[" +
                "title=" + title + ", " +
                "description=" + description + ']';
    }

    public String getImage() {
        return image;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAuthorUuid() {
        return authorUuid;
    }
}
