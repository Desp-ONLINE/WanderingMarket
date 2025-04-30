package org.desp.wanderingMarket.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.desp.sapphireMarket.database.ItemDataRepository;
import org.desp.sapphireMarket.database.ItemPurchaseLogRepository;
import org.desp.sapphireMarket.dto.ItemDataDto;
import org.desp.sapphireMarket.utils.ItemParser;
import org.jetbrains.annotations.NotNull;

public class WanderingMarketGUI implements InventoryHolder {

    private Player player;
    public Inventory inventory;

    public WanderingMarketGUI(Player player) {
        this.player = player;
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, 54, "사파이어샵");
        }
        Map<String, ItemDataDto> itemDataList = ItemDataRepository.getInstance().getItemDataList();

        for (Entry<String, ItemDataDto> entry : itemDataList.entrySet()) {
            String MMOItem_ID = entry.getKey();
            ItemDataDto itemDto = entry.getValue();

            int slot = itemDto.getSlot();

            int userTotalAmount = ItemPurchaseLogRepository.getInstance().countPurchaseLog(player, MMOItem_ID);
            int userTodayAmount = ItemPurchaseLogRepository.getInstance().countTodayPurchaseLog(player, MMOItem_ID);

            ItemStack item = ItemParser.getValidTypeItem(MMOItem_ID);
            ItemStack cloneItem = item.clone();
            cloneItem.setAmount(itemDto.getAmount());

            ItemMeta cloneItemMeta = cloneItem.getItemMeta();
            List<String> lore = new ArrayList<>();

            if (cloneItemMeta.hasLore()) {
                lore.addAll(cloneItemMeta.getLore());
            }

            boolean isBlocked = false;

            // 제한 조건 체크
            if (itemDto.getUserMaxPurchaseAmount() != -1 &&
                    userTotalAmount + itemDto.getAmount() > itemDto.getUserMaxPurchaseAmount()) {
                isBlocked = true;
            }

            if (itemDto.getUserDailyPurchaseAmount() != -1 &&
                    userTodayAmount + itemDto.getAmount() > itemDto.getUserDailyPurchaseAmount()) {
                isBlocked = true;
            }

            if (itemDto.getServerMaxPurchaseAmount() != -1 &&
                    0 >= itemDto.getServerMaxPurchaseAmount()) {
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

            lore.add("§f가격: §e" + itemDto.getPrice() + " §a사파이어");

            if (itemDto.getUserMaxPurchaseAmount() != -1) {
                int remainingUserTotal = Math.max(0, itemDto.getUserMaxPurchaseAmount() - userTotalAmount);
                lore.add("§f개인 총 한정: §e" + remainingUserTotal + "/" + itemDto.getUserMaxPurchaseAmount());
            }

            if (itemDto.getUserDailyPurchaseAmount() != -1) {
                int remainingToday = Math.max(0, itemDto.getUserDailyPurchaseAmount() - userTodayAmount);
                lore.add("§f일일 한정: §e" + remainingToday + "/" + itemDto.getUserDailyPurchaseAmount());
            }

            if (itemDto.getServerMaxPurchaseAmount() != -1) {
                int remainingServer = Math.max(0, itemDto.getServerMaxPurchaseAmount());
                lore.add("§f서버 남은 수량: §e" + remainingServer);
            }

            lore.add("");
            lore.add("§f----------------------");

            cloneItemMeta.setLore(lore);
            cloneItem.setItemMeta(cloneItemMeta);

            inventory.setItem(slot, cloneItem);
        }

        return inventory;
    }

}
