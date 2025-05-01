package org.desp.wanderingMarket.utils;

import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.desp.wanderingMarket.database.NPCLocationDataRepository;
import org.desp.wanderingMarket.dto.NPCLocationDto;

public class NPCWarpManager {

    private static NPCLocationDto currentNPCDto;

    public static void NPCSpawner() {
        NPCLocationDto npcLocationDto;

        // 다시 로드되는 NPC 의 위치는 이전과 다른 위치에 생성
        do {
            npcLocationDto = NPCLocationDataRepository.getInstance().getRandomNpcLocationDto();
        } while (npcLocationDto.equals(currentNPCDto));

        currentNPCDto = npcLocationDto;

        double x = npcLocationDto.getX();
        double y = npcLocationDto.getY();
        double z = npcLocationDto.getZ();

        double yaw = npcLocationDto.getYaw();
        double pitch = npcLocationDto.getPitch();

        // x,y,z,yaw,pitch 위치에 npc 스폰
        //Bukkit.getPlayer("Dawn__L").teleport(new Location(Bukkit.getWorld("world"), x, y, z));
//        Bukkit.getPlayer("Dawn__L").sendMessage("x : " + x + " y : " + y + " z : " + z);
        //Bukkit.getPlayer("Dawn__L").sendMessage(message);

        // 전체 알림
        //String message = npcLocationDto.getLocation() + "에 방랑 상인이 등장했습니다!!";
//        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, message);
//        Bukkit.broadcastMessage(message);
    }

    public static void NPCDelete() {
        // currentNPCDto 위치의 NPC 삭제
        //currentNPCDto = null;
    }
}
