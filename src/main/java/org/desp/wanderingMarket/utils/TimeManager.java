package org.desp.wanderingMarket.utils;

import java.time.LocalTime;
import java.util.Random;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.desp.wanderingMarket.WanderingMarket;
import org.desp.wanderingMarket.database.ItemDataRepository;
import org.desp.wanderingMarket.database.ItemPurchaseMemoryLogRepository;
import org.desp.wanderingMarket.gui.ItemPurchaseConfirmGUI;
import org.desp.wanderingMarket.gui.WanderingMarketGUI;

public class TimeManager {

    private static TimeManager instance;
    @Getter
    private int remainingTime;  //npc 유지시간

    private TimeManager() {}

    public static TimeManager getInstance() {
        if (instance == null) {
            instance = new TimeManager();
        }
        return instance;
    }

    public void startMarketRotationTask() {
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.NOON; // 12:00
        LocalTime end = LocalTime.of(23, 59, 59); // 23:59:59.000

        if (now.isBefore(start) || !now.isBefore(end)) {
            // 낮 12시 이전 또는 자정 이후면 실행하지 않음
            return;
        }
        int resetTime = getRandomTimeInterval();
        remainingTime = 600;

        // npc 등장하는 로직
        NPCWarpManager.NPCSpawner();

        ItemDataRepository.getInstance().getShuffledRandomItemDataList();
        ItemPurchaseMemoryLogRepository.getInstance().resetPurchaseMemoryLog();
        startCountdown();

        new BukkitRunnable() {
            @Override
            public void run() {
                startMarketRotationTask();
            }
        }.runTaskLater(WanderingMarket.getInstance(), 20L * resetTime);  // resetTime 초 후에 다시 실행
    }

    public void startCountdown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime > 0) {
                    remainingTime--;
                }
                if (remainingTime == 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Inventory topInv = player.getOpenInventory().getTopInventory();
                        if (topInv.getHolder() instanceof WanderingMarketGUI || topInv.getHolder() instanceof ItemPurchaseConfirmGUI) {
                            player.closeInventory();
                        }
                    }
                    // npc 제거하는 로직
                    NPCWarpManager.resetNPCLocation();
                    this.cancel();
                }
            }
        }.runTaskTimer(WanderingMarket.getInstance(), 20L, 20L); // 1초마다 실행
    }

    private int getRandomTimeInterval() {
        Random rand = new Random();
        int[] possibleIntervals = {7200, 10800}; // 2시간, 3시간
        return possibleIntervals[rand.nextInt(possibleIntervals.length)];
    }
}
