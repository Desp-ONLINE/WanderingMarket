package org.desp.wanderingMarket.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.desp.wanderingMarket.gui.ItemPurchaseConfirmGUI;
import org.desp.wanderingMarket.gui.WanderingMarketGUI;

public class ItemSelectListener implements Listener {

    @EventHandler
    public void onItemConfirm(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof WanderingMarketGUI)) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory.getType().equals(InventoryType.PLAYER) ) {
            return;
        }

        if (currentItem == null || currentItem.getType() == Material.AIR) return;
        if (event.getSlot() == 0) {
            return;
        }

        ItemPurchaseConfirmGUI itemPurchaseConfirmGUI = new ItemPurchaseConfirmGUI(currentItem);
        player.openInventory(itemPurchaseConfirmGUI.getInventory());
    }
}
