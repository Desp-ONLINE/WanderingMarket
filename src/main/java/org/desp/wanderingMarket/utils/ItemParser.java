package org.desp.wanderingMarket.utils;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.manager.TypeManager;
import org.bukkit.inventory.ItemStack;

public class ItemParser {

    public static ItemStack getValidTypeItem(String itemID) {
        ItemStack rewardItem = null;
        TypeManager types = MMOItems.plugin.getTypes();
        for (Type type : types.getAll()) {
            if(MMOItems.plugin.getItem(type, itemID)==null){
                continue;
            }
            rewardItem = MMOItems.plugin.getItem(type, itemID);
        }

        return rewardItem;
    }
}
