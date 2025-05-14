package org.desp.wanderingMarket.utils;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Random;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.desp.wanderingMarket.WanderingMarket;
import org.desp.wanderingMarket.database.ItemDataRepository;
import org.desp.wanderingMarket.database.ItemPurchaseMemoryLogRepository;
import org.desp.wanderingMarket.gui.ItemPurchaseConfirmGUI;
import org.desp.wanderingMarket.gui.WanderingMarketGUI;

public class TimeManager {

    private static TimeManager instance;
    @Getter
    private int remainingTime;  //npc 유지시간

    private TimeManager() {
    }

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
            // 낮 12시 이전 또는 자정 이후면 실행하지 않음 12시간 뒤에 예약
            runTaskLater(this::startMarketRotationTask, 12 * 3600 * 20);
            return;
        }
        int resetTime = getRandomTimeInterval();
        System.out.println("==============================");
        System.out.println("다음 상인 등장 시간 = " + resetTime);
        System.out.println("==============================");
        remainingTime = 600;

        NPCWarpManager.NPCSpawner();

        ItemDataRepository.getInstance().getShuffledRandomItemDataList();
        ItemPurchaseMemoryLogRepository.getInstance().resetPurchaseMemoryLog();
        startCountdown();

        runTaskLater(this::startMarketRotationTask, 20L * resetTime);
    }


    public void startCountdown() {
        int[] task = new int[1];
        task[0] = runTaskTimer(() -> {
            if (remainingTime > 0) {
                remainingTime--;
            }
            if (remainingTime == 0) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Inventory topInv = player.getOpenInventory().getTopInventory();
                    if (topInv.getHolder() instanceof WanderingMarketGUI
                            || topInv.getHolder() instanceof ItemPurchaseConfirmGUI) {
                        player.closeInventory();
                    }
                }

                NPCWarpManager.resetNPCLocation();

                runTaskLater(() -> {
                    Bukkit.getScheduler().cancelTask(task[0]);
                }, 20);
            }
        }, 20, 20);
    }

    private int runTaskLater(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(WanderingMarket.getInstance(), runnable, delay).getTaskId();
    }

    private int runTaskTimer(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(WanderingMarket.getInstance(), runnable, delay, period).getTaskId();
    }

    private int getRandomTimeInterval() {
        int[] intervals = {7200, 10800}; // 2시간 또는 3시간
        return intervals[new Random().nextInt(intervals.length)];
    }
}
