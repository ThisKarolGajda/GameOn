package com.gameon.plugin.features.stats;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.stats.StatType;
import com.gameon.plugin.GameOnPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class StatsListener implements Listener {
    private final StatsExtension statsExtension;

    public StatsListener(GameOnPlugin plugin, StatsExtension statsExtension) {
        this.statsExtension = statsExtension;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UserId userId = UserId.fromUuid(player.getUniqueId());
                    statsExtension.updatePlayerStats(userId, StatType.PLAYED_TIME, 1);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 20L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UserId userId = UserId.fromUuid(player.getUniqueId());
        statsExtension.updatePlayerStats(userId, StatType.DEATHS, 1);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UserId userId = UserId.fromUuid(player.getUniqueId());
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to != null && (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ())) {
            statsExtension.updatePlayerStats(userId, StatType.WALKED_DISTANCE, 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UserId userId = UserId.fromUuid(player.getUniqueId());
        statsExtension.updatePlayerStats(userId, StatType.MINED_BLOCKS, 1);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            UserId userId = UserId.fromUuid(killer.getUniqueId());
            if (event.getEntity() instanceof Player) {
                statsExtension.updatePlayerStats(userId, StatType.KILLS, 1);
            } else {
                statsExtension.updatePlayerStats(userId, StatType.MOB_KILLS, 1);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityBreed(EntityBreedEvent event) {
        Entity breeder = event.getBreeder();
        if (breeder instanceof Player player) {
            UserId userId = UserId.fromUuid(player.getUniqueId());
            statsExtension.updatePlayerStats(userId, StatType.ANIMAL_BREED, 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            UserId userId = UserId.fromUuid(player.getUniqueId());
            statsExtension.updatePlayerStats(userId, StatType.DEALT_DAMAGE, (long) event.getFinalDamage());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UserId userId = UserId.fromUuid(player.getUniqueId());
        statsExtension.updatePlayerStats(userId, StatType.PLACED_BLOCKS, 1);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        UserId userId = UserId.fromUuid(player.getUniqueId());
        statsExtension.updatePlayerStats(userId, StatType.XP_GAINED, event.getAmount());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        UserId userId = UserId.fromUuid(player.getUniqueId());
        ItemStack item = event.getItem();

        if (isTool(item.getType()) && item.getDurability() >= item.getType().getMaxDurability() - 1) {
            statsExtension.updatePlayerStats(userId, StatType.BROKEN_TOOLS, 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerThrowEnderPearl(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UserId userId = UserId.fromUuid(player.getUniqueId());

        if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR || event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            if (player.getInventory().getItemInMainHand().getType() == org.bukkit.Material.ENDER_PEARL) {
                statsExtension.updatePlayerStats(userId, StatType.THROWN_ENDER_PEARLS, 1);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCraftItem(@NotNull CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        UserId userId = UserId.fromUuid(player.getUniqueId());

        statsExtension.updatePlayerStats(userId, StatType.ITEMS_CRAFTED, getCraftedItem(event).getAmount());
    }

    @SuppressWarnings("deprecation")
    private ItemStack getCraftedItem(CraftItemEvent event) {
        if (event.isShiftClick()) {
            final ItemStack recipeResult = event.getRecipe().getResult();
            final int resultAmt = recipeResult.getAmount();
            int leastIngredient = -1;
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item != null && !item.getType().equals(Material.AIR)) {
                    final int result = item.getAmount() * resultAmt;
                    if (leastIngredient == -1 || result < leastIngredient) {
                        leastIngredient = item.getAmount() * resultAmt;
                    }
                }
            }
            return new ItemStack(recipeResult.getType(), leastIngredient, recipeResult.getDurability());
        }
        return event.getCurrentItem();
    }

    private boolean isTool(Material material) {
        return material.toString().endsWith("_PICKAXE") || material.toString().endsWith("_AXE") ||
                material.toString().endsWith("_SHOVEL") || material.toString().endsWith("_HOE");
    }
}
