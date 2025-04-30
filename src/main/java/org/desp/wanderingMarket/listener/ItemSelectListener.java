package org.desp.wanderingMarket.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.desp.sapphireMarket.gui.ItemPurchaseConfirmGUI;
import org.desp.sapphireMarket.gui.SapphireMarketGUI;

public class ItemSelectListener implements Listener {

    @EventHandler
    public void onItemConfirm(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SapphireMarketGUI)) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();

        if (currentItem == null || currentItem.getType() == Material.AIR) return;

        ItemPurchaseConfirmGUI itemPurchaseConfirmGUI = new ItemPurchaseConfirmGUI(currentItem);
        player.openInventory(itemPurchaseConfirmGUI.getInventory());
    }
}
