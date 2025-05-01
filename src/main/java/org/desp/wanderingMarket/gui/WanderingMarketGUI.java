package org.desp.wanderingMarket.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.desp.wanderingMarket.WanderingMarket;
import org.desp.wanderingMarket.database.ItemDataRepository;
import org.desp.wanderingMarket.database.ItemPurchaseMemoryLogRepository;
import org.desp.wanderingMarket.dto.ItemDataDto;
import org.desp.wanderingMarket.utils.ItemParser;
import org.desp.wanderingMarket.utils.TimeManager;
import org.jetbrains.annotations.NotNull;

public class WanderingMarketGUI implements InventoryHolder {

    static {
        Bukkit.getScheduler().runTaskTimer(WanderingMarket.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Inventory topInv = player.getOpenInventory().getTopInventory();
                if (!(topInv.getHolder() instanceof WanderingMarketGUI gui)) {
                    continue;
                }
                gui.refreshTimeItem();
            }
        }, 20, 20);
    }

    private final Player player;
    private final Inventory inventory;
    public WanderingMarketGUI(Player player) {
        this.player = player;
        this.inventory = create();
        refreshTimeItem();
    }

    private Inventory create() {
        TextComponent title = Component.text("                     방랑 상인 상점");
        Inventory inventory = Bukkit.createInventory(this, 27, title);

        List<ItemDataDto> shuffledItemDataList = ItemDataRepository.getInstance().getShuffledItemDataList();

        int slot = 10;

        for (ItemDataDto itemDto : shuffledItemDataList) {
            int userTotalAmount = ItemPurchaseMemoryLogRepository.getInstance()
                    .countPurchaseLog(player, itemDto.getItemID());

            ItemStack item = ItemParser.getValidTypeItem(itemDto.getMMOItem_ID());
            ItemStack cloneItem = item.clone();
            cloneItem.setAmount(itemDto.getAmount());

            ItemMeta cloneItemMeta = cloneItem.getItemMeta();
            List<String> lore = new ArrayList<>();

            if (cloneItemMeta.hasLore()) {
                lore.addAll(cloneItemMeta.getLore());
            }

            boolean isBlocked = false;

            if (itemDto.getUserMaxPurchaseAmount() != -1 &&
                    userTotalAmount + 1 > itemDto.getUserMaxPurchaseAmount()) {
                isBlocked = true;
            }

            // 구매 불가 표시 추가
            lore.add("");
            lore.add("§f----------------------");
            lore.add("");

            if (isBlocked) {
                lore.add("§c◆ 구매 불가: 제한 수량 초과 ◆");
                lore.add("");
            }

            lore.add("§f가격: §e" + itemDto.getPrice() + " §a골드");

            if (itemDto.getUserMaxPurchaseAmount() != -1) {
                int remainingUserTotal = Math.max(0, itemDto.getUserMaxPurchaseAmount() - userTotalAmount);
                lore.add("§f개인 총 한정: §e" + remainingUserTotal + "/" + itemDto.getUserMaxPurchaseAmount());
            }

            lore.add("");
            lore.add("§f----------------------");

            cloneItemMeta.setLore(lore);
            cloneItem.setItemMeta(cloneItemMeta);

            inventory.setItem(slot, cloneItem);
            slot++;
        }

        return inventory;
    }

    private void refreshTimeItem() {
        ItemStack timeViewer = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemMeta timeItemMeta = timeViewer.getItemMeta();

        if (timeItemMeta != null) {
            List<String> lore = new ArrayList<>();
            // 여기만 수정하면 됩다

            int remainingTime = TimeManager.getInstance().getRemainingTime();
            int minutes = remainingTime / 60;
            int seconds = remainingTime % 60;
            lore.add("§f남은 시간: §e" + minutes + "분 " + seconds + "초");

//            int remainingTime = TimeManager.getInstance().getRemainingTime();
//            lore.add("§f남은 시간: §e" + remainingTime + "초");
            timeItemMeta.setLore(lore);
            timeViewer.setItemMeta(timeItemMeta);
        }
        inventory.setItem(0, timeViewer);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}