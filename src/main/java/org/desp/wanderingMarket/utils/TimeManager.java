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
    @Getter
    private int remainingTime;  //npc 유지시간
    private boolean canRun = true;

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

        int resetTime = getRandomTimeInterval();

        LocalTime nextExecutionTime = now.plusSeconds(resetTime);  // 현재 시간 + resetTime 초

        // 만약 예약 시간이 12시 이전이라면, 12시로 강제로 설정
        if (nextExecutionTime.isBefore(start)) {
            canRun = false;
            System.out.println("before 12");
            nextExecutionTime = start;  // 예약 시간을 12시로 설정
            resetTime = (int) Duration.between(now, nextExecutionTime).toSeconds();  // 12시로부터의 초 계산
            System.out.println("resetTime = " + resetTime);
        }

        // 현재 시간에 resetTime을 더했을 때 자정을 넘으면 12시간을 더해주는 방식으로 처리
        if (nextExecutionTime.isAfter(end)) {
            canRun = false;
            System.out.println("resetTime is after end");
            resetTime += 12 * 3600;  // 12시간(43200초) 더해서 예약
            System.out.println("resetTime = " + resetTime);
        }

        // 예약 시간이 지나면 canRun을 다시 true로 설정
        if (nextExecutionTime.isAfter(start) && nextExecutionTime.isBefore(end)) {
            canRun = true;  // 조건에 맞으면 작업 실행
        }

        remainingTime = 600;

        if (canRun) {
            // npc 등장하는 로직
            NPCWarpManager.NPCSpawner();

            ItemDataRepository.getInstance().getShuffledRandomItemDataList();
            ItemPurchaseMemoryLogRepository.getInstance().resetPurchaseMemoryLog();
            startCountdown();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                startMarketRotationTask();
            }
        }.runTaskLater(WanderingMarket.getInstance(), 20L * resetTime);  // resetTime 초 후에 다시 실행
    }

//      문제 생기면 여기껄로 돌게 해주세요 대신 낮 12시 ~ 밤 24시 사이에 안돌게 하는거 추가 안되있어서 그사이에 직접 reload, 24시 넘으면 unload 해줘야함 우하하
//    public void startMarketRotationTask() {
//        LocalTime now = LocalTime.now();
//        LocalTime start = LocalTime.NOON; // 12:00
//        LocalTime end = LocalTime.of(23, 59, 59); // 23:59:59.000
//
//        int resetTime = getRandomTimeInterval();
//
//        remainingTime = 600;
//
//        NPCWarpManager.NPCSpawner();
//
//        ItemDataRepository.getInstance().getShuffledRandomItemDataList();
//        ItemPurchaseMemoryLogRepository.getInstance().resetPurchaseMemoryLog();
//        startCountdown();
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                startMarketRotationTask();
//            }
//        }.runTaskLater(WanderingMarket.getInstance(), 20L * resetTime);  // resetTime 초 후에 다시 실행
//    }

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
