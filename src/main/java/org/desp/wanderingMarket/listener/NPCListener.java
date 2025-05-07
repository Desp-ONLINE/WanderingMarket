package org.desp.wanderingMarket.listener;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.swlab.etcetera.Util.CommandUtil;

public class NPCListener implements Listener {

    @EventHandler
    public void onPlayerClickNPC(NPCRightClickEvent event) {
        Player player = event.getClicker();
        if (event.getNPC().getId() == 214) {
            CommandUtil.runCommandAsOP(player, "방랑상인상점");
        }
    }
}
