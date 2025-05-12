package org.desp.wanderingMarket.utils;

import java.time.Duration;
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

    public static TimeManager getInstance() {
        if (instance == null) {
            instance = new TimeManager();
        }
        return instance;
    }

    @Getter
    private int remainingTime;

    private BukkitRunnable marketRotationTask;
    private BukkitRunnable countdownTask;

    private TimeManager() {}

    public void startMarketRotationTask() {
        if (marketRotationTask != null && !marketRotationTask.isCancelled()) {
            System.out.println("marketRotationTask cancel");
            marketRotationTask.cancel();
        }

        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.NOON; // 낮 12시
        LocalTime end = LocalTime.of(23, 59, 59); // 밤 11시 59분

        int resetTime = getRandomTimeInterval(); // 2시간 또는 3시간
        LocalTime nextExecutionTime = now.plusSeconds(resetTime);

        System.out.println("------------------------------");
        System.out.println("Current Time: " + now);
        System.out.println("Next Execution Time: " + nextExecutionTime);
        System.out.println("resetTime = " + resetTime);
        System.out.println("------------------------------");

        if (now.isAfter(start) && now.isBefore(end)) {
            // 현재가 12시~자정 사이일 때만 spawn
            System.out.println("정상 오픈 시간");
            spawnMarket();

            marketRotationTask = new BukkitRunnable() {
                @Override
                public void run() {
                    System.out.println("Running market rotation task.");
                    startMarketRotationTask();
                }
            };
            marketRotationTask.runTaskLater(WanderingMarket.getInstance(), 20L * resetTime);

        } else {
            // 현재 시간이 자정~정오일 경우, 12시까지 기다린다
            long delayUntilNoon = Duration.between(now, start).getSeconds();

            System.out.println("상점 문닫는 시간");

            marketRotationTask = new BukkitRunnable() {
                @Override
                public void run() {
                    System.out.println("낮 12시라서 상점 오픈");
                    spawnMarket();
                    startMarketRotationTask();
                }
            };
            marketRotationTask.runTaskLater(WanderingMarket.getInstance(), 20L * delayUntilNoon);
        }

    }

    private void spawnMarket() {
        remainingTime = 600; // 10분
        NPCWarpManager.NPCSpawner();
        ItemDataRepository.getInstance().getShuffledRandomItemDataList();
        ItemPurchaseMemoryLogRepository.getInstance().resetPurchaseMemoryLog();
        startCountdown();
    }

    public void startCountdown() {
        if (countdownTask != null && !countdownTask.isCancelled()) {
            System.out.println("countdownTask cancel");
            countdownTask.cancel();
        }

        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime > 0) {
                    remainingTime--;
                }

                if (remainingTime == 0) {
                    System.out.println("상인 유지시간 종료 npc 위치 초기화");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Inventory topInv = player.getOpenInventory().getTopInventory();
                        if (topInv.getHolder() instanceof WanderingMarketGUI ||
                                topInv.getHolder() instanceof ItemPurchaseConfirmGUI) {
                            player.closeInventory();
                        }
                    }
                    NPCWarpManager.resetNPCLocation();
                    this.cancel();
                }
            }
        };
        countdownTask.runTaskTimer(WanderingMarket.getInstance(), 20L, 20L); // 1초마다
    }

    private int getRandomTimeInterval() {
        int[] intervals = {7200, 10800}; // 2시간 또는 3시간
        return intervals[new Random().nextInt(intervals.length)];
    }
}
