package org.desp.wanderingMarket.utils;

import java.util.Arrays;
import java.util.List;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.inventory.ItemStack;

public class ItemParser {

    public static ItemStack getValidTypeItem(String itemID) {
        List<Type> itemTypes = Arrays.asList(Type.SWORD, Type.ARMOR, Type.MISCELLANEOUS, Type.CONSUMABLE, Type.get("TITLE"));

        for (Type type : itemTypes) {
            ItemStack rewardItem = MMOItems.plugin.getItem(type, itemID);
            if (rewardItem != null) {
                return rewardItem;
            }
        }
        return null;
    }
}
