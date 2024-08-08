package com.gameon.api.server.features.dailyreward;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.IExtension;

public interface IDailyRewardExtension extends IExtension {

    /**
     * Checks if the specified user is eligible for a daily reward.
     *
     * @param userId the ID of the user to check
     * @return true if the user is eligible for the daily reward, false otherwise
     */
    boolean hasClaimed(UserId userId);

    /**
     * Allows the specified user to claim their daily reward.
     *
     * @param userId the ID of the user claiming the reward
     * @return true if the user successfully claimed the reward, false if they have already claimed it
     */
    boolean claim(UserId userId);
}
