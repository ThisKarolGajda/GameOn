package com.gameon.plugin.features.player.activity;

import com.gameon.api.server.common.UserId;
import com.gameon.api.server.features.player.activity.PlayerActivityType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.block.Smoker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ActivityListener implements Listener {
    private final ActivityPointsManager activityPointsManager;
    private final Cache<UserId, Set<byte[]>> exploredChunks;
    private final Plugin plugin;
    private final Map<UserId, Long> lastActivityTime = new HashMap<>();

    public ActivityListener(Plugin plugin, ActivityPointsManager activityPointsManager) {
        this.plugin = plugin;
        this.activityPointsManager = activityPointsManager;

        this.exploredChunks = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(15, TimeUnit.MINUTES)
                .build();

        startNearbyPlayerCheckTask();
        startIdleCheckTask();
    }

    // Mining
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        UserId userId = UserId.fromUuid(event.getPlayer().getUniqueId());
        Material blockType = event.getBlock().getType();

        if (isMiningBlock(blockType)) {
            int points = getMiningPoints(blockType);
            activityPointsManager.addPoints(userId, PlayerActivityType.MINING, points);
        }
    }

    @Contract(pure = true)
    private boolean isMiningBlock(@NotNull Material blockType) {
        return switch (blockType) {
            case COAL_ORE, IRON_ORE, GOLD_ORE, DIAMOND_ORE, EMERALD_ORE, LAPIS_ORE, REDSTONE_ORE, COPPER_ORE,
                 STONE, DIORITE, GRANITE, ANDESITE, DEEPSLATE, TUFF, CALCITE,
                 NETHERRACK, SOUL_SAND, SOUL_SOIL, END_STONE, OBSIDIAN,
                 BLACKSTONE, BASALT, GILDED_BLACKSTONE, DEEPSLATE_COAL_ORE,
                 DEEPSLATE_IRON_ORE, DEEPSLATE_GOLD_ORE, DEEPSLATE_DIAMOND_ORE,
                 DEEPSLATE_EMERALD_ORE, DEEPSLATE_LAPIS_ORE, DEEPSLATE_REDSTONE_ORE,
                 DEEPSLATE_COPPER_ORE -> true;
            default -> false;
        };
    }

    @Contract(pure = true)
    private int getMiningPoints(@NotNull Material blockType) {
        return switch (blockType) {
            case DIAMOND_ORE, EMERALD_ORE -> 10;
            case GOLD_ORE, LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> 5;
            case COAL_ORE, IRON_ORE, COPPER_ORE, REDSTONE_ORE, DEEPSLATE_COAL_ORE, DEEPSLATE_IRON_ORE,
                 DEEPSLATE_COPPER_ORE -> 2;
            case STONE, DIORITE, GRANITE, ANDESITE, DEEPSLATE, TUFF, CALCITE, NETHERRACK, SOUL_SAND, SOUL_SOIL,
                 END_STONE, OBSIDIAN, BLACKSTONE, BASALT, GILDED_BLACKSTONE -> 1;
            default -> 0;
        };
    }

    // Building
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        UserId userId = UserId.fromUuid(event.getPlayer().getUniqueId());
        Material blockType = event.getBlock().getType();

        if (isBuildingBlock(blockType)) {
            int points = getBuildingPoints(blockType);
            activityPointsManager.addPoints(userId, PlayerActivityType.BUILDING, points);
        }
    }

    private boolean isBuildingBlock(Material blockType) {
        return true;
    }

    private int getBuildingPoints(Material blockType) {
        return 1;
    }

    // Fighting
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (damager instanceof Player player && victim instanceof Player) {
            UserId userId = UserId.fromUuid(player.getUniqueId());
            activityPointsManager.addPoints(userId, PlayerActivityType.FIGHTING, 2);
        } else if (damager instanceof Player player) {
            UserId userId = UserId.fromUuid(player.getUniqueId());
            activityPointsManager.addPoints(userId, PlayerActivityType.FIGHTING, 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            UserId userId = UserId.fromUuid(killer.getUniqueId());
            activityPointsManager.addPoints(userId, PlayerActivityType.FIGHTING, 17);
        }
    }

    // Exploration
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UserId userId = UserId.fromUuid(player.getUniqueId());
        exploredChunks.put(userId, new HashSet<>());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UserId userId = UserId.fromUuid(player.getUniqueId());
        Chunk currentChunk = player.getLocation().getChunk();
        byte[] currentCoordinate = new byte[]{(byte) currentChunk.getX(), (byte) currentChunk.getZ()};

        Set<byte[]> playerExploredChunks = exploredChunks.getIfPresent(userId);
        if (playerExploredChunks != null && !containsChunkCoordinate(playerExploredChunks, currentCoordinate)) {
            playerExploredChunks.add(currentCoordinate);
            activityPointsManager.addPoints(userId, PlayerActivityType.EXPLORING, 5);
        }
    }

    private boolean containsChunkCoordinate(Set<byte[]> chunkSet, byte[] coordinate) {
        for (byte[] chunk : chunkSet) {
            if (chunk[0] == coordinate[0] && chunk[1] == coordinate[1]) {
                return true;
            }
        }
        return false;
    }

    // Farming
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFarmingBlockBreak(BlockBreakEvent event) {
        UserId userId = UserId.fromUuid(event.getPlayer().getUniqueId());
        Material blockType = event.getBlock().getType();

        if (isFarmingBlock(blockType)) {
            activityPointsManager.addPoints(userId, PlayerActivityType.FARMING, 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAnimalBreed(EntityBreedEvent event) {
        if (event.getBreeder() == null) {
            return;
        }
        UserId userId = UserId.fromUuid(event.getBreeder().getUniqueId());
        activityPointsManager.addPoints(userId, PlayerActivityType.FARMING, 5);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerHarvest(PlayerHarvestBlockEvent event) {
        UserId userId = UserId.fromUuid(event.getPlayer().getUniqueId());
        Material blockType = event.getHarvestedBlock().getType();

        if (isFarmingBlock(blockType)) {
            activityPointsManager.addPoints(userId, PlayerActivityType.FARMING, 2);
        }
    }

    private boolean isFarmingBlock(Material blockType) {
        return switch (blockType) {
            case WHEAT, CARROTS, POTATOES, BEETROOTS, NETHER_WART, COCOA, PUMPKIN_STEM, MELON_STEM, SUGAR_CANE,
                 BAMBOO -> true;
            default -> false;
        };
    }

    // Trading
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTrade(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();

        if (topInventory.getType() == InventoryType.MERCHANT) {
            Player player = (Player) event.getWhoClicked();
            UserId userId = UserId.fromUuid(player.getUniqueId());

            activityPointsManager.addPoints(userId, PlayerActivityType.TRADING, 1);
        }
    }

    // Crafting
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCraft(CraftItemEvent event) {
        UserId userId = UserId.fromUuid(event.getWhoClicked().getUniqueId());
        ItemStack craftedItem = event.getCurrentItem();

        if (craftedItem != null) {
            activityPointsManager.addPoints(userId, PlayerActivityType.CRAFTING, craftedItem.getAmount());
        }
    }

    // Cooking
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Furnace || event.getInventory().getHolder() instanceof Smoker) {
            Player player = (Player) event.getWhoClicked();
            UserId userId = UserId.fromUuid(player.getUniqueId());

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                activityPointsManager.addPoints(userId, PlayerActivityType.COOKING, 1); // Award points for cooking
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCampfireCook(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && block.getType() == Material.CAMPFIRE) {
                UserId userId = UserId.fromUuid(event.getPlayer().getUniqueId());
                activityPointsManager.addPoints(userId, PlayerActivityType.COOKING, 1); // Award points for campfire cooking
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMobKillWithFire(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            UserId userId = UserId.fromUuid(event.getEntity().getKiller().getUniqueId());
            EntityType entityType = event.getEntityType();

            if (entityType == EntityType.COW || entityType == EntityType.PIG || entityType == EntityType.CHICKEN) {
                activityPointsManager.addPoints(userId, PlayerActivityType.COOKING, 1); // Award points for killing mobs with fire
            }
        }
    }

    // Fishing
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        UserId userId = UserId.fromUuid(player.getUniqueId());

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            activityPointsManager.addPoints(userId, PlayerActivityType.FISHING, 5);
        } else if (event.getState() == PlayerFishEvent.State.FISHING) {
            activityPointsManager.addPoints(userId, PlayerActivityType.FISHING, 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFishKill(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.SALMON || event.getEntityType() == EntityType.COD ||
                event.getEntityType() == EntityType.TROPICAL_FISH || event.getEntityType() == EntityType.PUFFERFISH) {
            if (event.getEntity().getKiller() != null) {
                UserId userId = UserId.fromUuid(event.getEntity().getKiller().getUniqueId());
                activityPointsManager.addPoints(userId, PlayerActivityType.FISHING, 3);
            }
        }
    }

    // Enchanting
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEnchant(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ENCHANTING_TABLE) {
            Player player = event.getPlayer();
            UserId userId = UserId.fromUuid(player.getUniqueId());
            activityPointsManager.addPoints(userId, PlayerActivityType.ENCHANTING, 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEnchantItem(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.ENCHANTING) {
            Player player = (Player) event.getWhoClicked();
            UserId userId = UserId.fromUuid(player.getUniqueId());

            ItemStack item = event.getCurrentItem();
            if (item != null && item.getType() != Material.AIR) {
                activityPointsManager.addPoints(userId, PlayerActivityType.ENCHANTING, 2);
            }
        }
    }

    // Socializing
    private void startNearbyPlayerCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Set<Player> players = new HashSet<>(Bukkit.getOnlinePlayers());
                for (Player player : players) {
                    UserId userId = UserId.fromUuid(player.getUniqueId());
                    for (Player otherPlayer : players) {
                        if (!otherPlayer.equals(player) && player.getLocation().distance(otherPlayer.getLocation()) <= 25) {
                            activityPointsManager.addPoints(userId, PlayerActivityType.SOCIALIZING, 1);
                            break;
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 200L);
    }

    // Idle
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerChatIdle(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UserId userId = UserId.fromUuid(player.getUniqueId());
        activityPointsManager.addPoints(userId, PlayerActivityType.SOCIALIZING, 1);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMoveIdle(PlayerMoveEvent event) {
        updateLastActivity(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        updateLastActivity(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        updateLastActivity(event.getPlayer());
    }

    private void updateLastActivity(Player player) {
        UserId userId = UserId.fromUuid(player.getUniqueId());
        lastActivityTime.put(userId, System.currentTimeMillis());
    }

    private void startIdleCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UserId userId = UserId.fromUuid(player.getUniqueId());
                    long lastActivity = lastActivityTime.getOrDefault(userId, currentTime);
                    if ((currentTime - lastActivity) >= 300 * 1000) {
                        activityPointsManager.addPoints(userId, PlayerActivityType.IDLE, 1);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 200L);
    }
}
