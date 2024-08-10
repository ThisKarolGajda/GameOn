package com.gameon.api.server.features.stats;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.extension.IExtension;

public interface IStatsExtension extends IExtension {

    long getValueSummed(StatType statType);

    long getValue(StatType statType, UserId userId);
}
