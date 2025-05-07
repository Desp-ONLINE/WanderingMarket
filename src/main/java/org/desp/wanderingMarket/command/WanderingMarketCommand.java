package org.desp.wanderingMarket.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.desp.wanderingMarket.gui.WanderingMarketGUI;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Util.CommandUtil;

public class WanderingMarketCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {

        if (!(commandSender instanceof Player player)) {
            return false;
        }
        if (!player.isOp()) {
            return false;
        }
        WanderingMarketGUI wanderingMarketGUI = new WanderingMarketGUI(player);
        player.openInventory(wanderingMarketGUI.getInventory());
        return false;
    }
}
