package org.desp.wanderingMarket.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ItemPurchaseConfirmGUI implements InventoryHolder {

    public Inventory inventory;
    private static ItemStack item;

    public ItemPurchaseConfirmGUI(ItemStack item) {this.item = item;}

    @Override
    public @NotNull Inventory getInventory() {
        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, 27, "구매확인");
        }

        ItemStack redPane = createPane(Material.RED_STAINED_GLASS_PANE, "구매취소");
        ItemStack greenPane = createPane(Material.GREEN_STAINED_GLASS_PANE, "구매");
        ItemStack whitePane = createPane(Material.WHITE_STAINED_GLASS_PANE, "*");

        for (int i : new int[]{0, 1, 2, 9, 10, 11, 18, 19, 20}) {
            inventory.setItem(i, redPane);
        }

        for (int i : new int[]{3, 4, 5, 12, 14, 21, 22, 23}) {
            inventory.setItem(i, whitePane);
        }

        inventory.setItem(13, item);

        for (int i : new int[]{6, 7, 8, 15, 16, 17, 24, 25, 26}) {
            inventory.setItem(i, greenPane);
        }

        return inventory;
    }

    private ItemStack createPane(Material material, String name) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(name);
        pane.setItemMeta(meta);
        return pane;
    }
}
