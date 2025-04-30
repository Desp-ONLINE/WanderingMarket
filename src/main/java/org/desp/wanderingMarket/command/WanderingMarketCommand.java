package org.desp.wanderingMarket.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.desp.sapphireMarket.gui.SapphireMarketGUI;
import org.jetbrains.annotations.NotNull;

public class WanderingMarketCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {

        if (!(commandSender instanceof Player player)) {
            return false;
        }

        SapphireMarketGUI sapphireMarketGUI = new SapphireMarketGUI(player);
        player.openInventory(sapphireMarketGUI.getInventory());
        return false;
    }
}
