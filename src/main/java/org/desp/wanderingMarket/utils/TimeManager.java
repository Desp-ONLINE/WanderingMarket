package org.desp.wanderingMarket.utils;

import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
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
        int resetTime = getRandomTimeInterval();
        remainingTime = 10;

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
                    NPCWarpManager.NPCDelete();
                    this.cancel();
                }
            }
        }.runTaskTimer(WanderingMarket.getInstance(), 20L, 20L); // 1초마다 실행
    }

    private int getRandomTimeInterval() {
        Random rand = new Random();
        //int[] possibleIntervals = {3600, 7200, 10800}; // 1시간, 2시간, 3시간
        int[] possibleIntervals = {20, 30, 15}; // 테스트용 20, 30, 15초
        return possibleIntervals[rand.nextInt(possibleIntervals.length)];
    }
}
