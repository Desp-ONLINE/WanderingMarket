package org.desp.wanderingMarket.listener;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.desp.wanderingMarket.utils.NPCWarpManager;

public class NPCListener implements Listener {

    @EventHandler
    public void onPlayerClickNPC(NPCRightClickEvent event) {
        Player player = event.getClicker();
        if (event.getNPC().getId() == 214) {
            player.sendMessage("Clicked NPC");
            player.performCommand("방랑상인상점");  // or Bukkit.dispatchCommand(...)
        }
    }
}
