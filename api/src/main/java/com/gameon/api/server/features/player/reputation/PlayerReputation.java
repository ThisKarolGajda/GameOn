package com.gameon.api.server.features.player.reputation;

import com.gameon.api.server.common.UserId;

import java.util.ArrayList;
import java.util.List;

public class PlayerReputation {
    private final List<UserId> liked;
    private final List<UserId> disliked;

    public PlayerReputation(List<UserId> liked, List<UserId> disliked) {
        this.liked = liked;
        this.disliked = disliked;
    }

    public PlayerReputation() {
        this.liked = new ArrayList<>();
        this.disliked = new ArrayList<>();
    }

    public List<UserId> getLiked() {
        return liked;
    }

    public List<UserId> getDisliked() {
        return disliked;
    }

    public void addLiked(UserId userId) {
        liked.add(userId);
    }

    public void addDisliked(UserId userId) {
        disliked.add(userId);
    }
}
