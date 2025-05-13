package org.desp.wanderingMarket.utils;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import com.binggre.velocitysocketclient.listener.BroadcastTitleVelocityListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.desp.wanderingMarket.database.NPCLocationDataRepository;
import org.desp.wanderingMarket.dto.NPCLocationDto;

public class NPCWarpManager {

    @Getter
    private static NPCLocationDto currentNPCDto;
    @Getter
    private static NPC wanderingNPC;

    private static final Location DEFAULT_LOCATION = new Location(
            Bukkit.getWorld("world"),
            101.529,
            266.0000,
            -731.620
    );

    public static void createNPC() {
        if (CitizensAPI.getNPCRegistry().getById(214) == null) {
            wanderingNPC = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "방랑상인");
            wanderingNPC.setProtected(true);
            wanderingNPC.spawn(DEFAULT_LOCATION); // 기본 대기 위치
        } else {
            wanderingNPC = CitizensAPI.getNPCRegistry().getById(214);
            wanderingNPC.spawn(DEFAULT_LOCATION); // 기본 대기 위치
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
            wanderingNPC.teleport(targetLocation, TeleportCause.PLUGIN);
        }

        String village = "";
        // 전체 알림
        if ("엘븐하임".equals(npcLocationDto.getLocation())) {
            village = "#A4F454엘#8CE682븐#73D7B0하#5BC9DE임";
        } else if ("칼리마".equals(npcLocationDto.getLocation())) {
            village = "#C8AB30칼#D3A046리#DE955B마";
        } else if ("인페리움".equals(npcLocationDto.getLocation())) {
            village = "#D23939인#D86C66페#DF9E93리#E5D1C0움";
        }

        String format = ColorManager.format(village);

        String message = "§f  "+format + "§f에 방랑 상인이 출현했습니다!";
        String divideLine = ColorManager.format("#FFB656§m                                                                §f");
        String emptyLine = ColorManager.format("");

        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, divideLine);
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, emptyLine);
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, message);
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, emptyLine);
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, divideLine);
        Bukkit.broadcastMessage(divideLine);
        Bukkit.broadcastMessage(emptyLine);
        Bukkit.broadcastMessage(message);
        Bukkit.broadcastMessage(emptyLine);
        Bukkit.broadcastMessage(divideLine);
    }

    public static void resetNPCLocation() {
        String message = "§c방랑 상인이 사라졌습니다.. 다른 곳에 등장할 수도 있어요";

        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, message);
        Bukkit.broadcastMessage(message);

        if (!wanderingNPC.isSpawned()) {
            wanderingNPC.spawn(DEFAULT_LOCATION, SpawnReason.RESPAWN);
        }
        wanderingNPC.teleport(DEFAULT_LOCATION, TeleportCause.PLUGIN);
    }
}
