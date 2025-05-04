package org.desp.wanderingMarket.utils;

import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import java.util.Map;
import lombok.Getter;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.desp.wanderingMarket.database.NPCLocationDataRepository;
import org.desp.wanderingMarket.dto.NPCLocationDto;

public class NPCWarpManager {

    private static NPCLocationDto currentNPCDto;
    @Getter
    private static NPC wanderingNPC;

    public static void createNPC() {
        if (CitizensAPI.getNPCRegistry().getById(214) == null) {
            wanderingNPC = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "방랑상인");
            wanderingNPC.setProtected(true);
            wanderingNPC.spawn(new Location(Bukkit.getWorld("world"), 101.529, 266.0000, -731.620)); // 기본 대기 위치
        }else {
            wanderingNPC = CitizensAPI.getNPCRegistry().getById(214);
            wanderingNPC.spawn(new Location(Bukkit.getWorld("world"), 101.529, 266.0000, -731.620)); // 기본 대기 위치
        }
    }

    public static void NPCSpawner() {
        NPCLocationDto npcLocationDto;

        // 다시 로드되는 NPC 의 위치는 이전과 다른 위치에 생성
        do {
            npcLocationDto = NPCLocationDataRepository.getInstance().getRandomNpcLocationDto();
        } while (npcLocationDto.equals(currentNPCDto));

        currentNPCDto = npcLocationDto;

        Location targetLocation = new Location(
                Bukkit.getWorld("world"),
                npcLocationDto.getX(),
                npcLocationDto.getY(),
                npcLocationDto.getZ(),
                (float) npcLocationDto.getYaw(),
                (float) npcLocationDto.getPitch()
        );

        if (wanderingNPC != null) {
            wanderingNPC.teleport(targetLocation, TeleportCause.UNKNOWN);
        }

        // 전체 알림
        String message = npcLocationDto.getLocation() + "에 방랑 상인이 등장했습니다!!";
        Bukkit.getPlayer("Dawn__L").sendMessage(message);
        //Bukkit.getPlayer("Dawn__L").teleport(targetLocation);
//        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, message);

//        Bukkit.broadcastMessage(message);
    }

    public static void resetNPCLocation() {
        System.out.println("NPC reset Location");
//        Bukkit.getPlayer("Dawn__L").teleport(new Location(Bukkit.getWorld("world"), 151.472, 258.00000, -715.576));
        String message = "방랑상인이 사라졌습니다";
        Bukkit.getPlayer("Dawn__L").sendMessage(message);
        wanderingNPC.teleport(new Location(Bukkit.getWorld("world"), 101.529, 266.0000, -731.620), TeleportCause.UNKNOWN);
    }
}
